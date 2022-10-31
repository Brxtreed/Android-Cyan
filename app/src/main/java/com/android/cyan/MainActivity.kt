package com.android.cyan

import DeviceAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.OnItemClickListener
import kotlinx.coroutines.InternalCoroutinesApi
import viewModels.CyanViewModel
import java.io.IOException
import java.util.*


val REQUEST_COARSE_LOCATION = 101
//lateinit var deviceAdapter: ArrayAdapter<String>

private lateinit var startBluetoothScanPermissionLauncher: ActivityResultLauncher<String>
private lateinit var stopBluetoothScanPermissionLauncher: ActivityResultLauncher<String>
private lateinit var deviceAdapter: GroupAdapter<GroupieViewHolder>


@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    // val cyanAdapter = CyanAdapter()
    private lateinit var disableButton: Button
    private lateinit var enableButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: CyanViewModel
    lateinit var bluetoothStatus: TextView
    lateinit var deviceListview: RecyclerView

    private lateinit var bluetoothAdapter: BluetoothManager
    var deviceNameList = mutableListOf<BluetoothDevice>()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bluetoothAdapter = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        viewModel = ViewModelProvider(this).get(CyanViewModel::class.java)
        initViews()
        checkPermissions()
        bindBroadcast()
        initListeners()

        val thread = AcceptThread()
        thread.start()

//        lifecycleScope.launch {
//            cyanAdapter.bluetoothStatus
//                .collect {
//                    bluetoothStatus.text = it.toString()
//                    // New location! Update the map
//                }
//        }
    }

    private fun initListeners() {
        enableButton.setOnClickListener {
            deviceNameList.clear()
            enableBluetooth()
        }

        disableButton.setOnClickListener {
            disableBluetooth()

        }
    }

    private fun bindBroadcast() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
            addAction(ACTION_DISCOVERY_FINISHED)
            addAction(ACTION_DISCOVERY_STARTED)
        }
        registerReceiver(receiver, filter)
    }

    @SuppressLint("MissingPermission")
    private fun checkPermissions() {
        val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            ), MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
        )
        startBluetoothScanPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                bluetoothAdapter.adapter.startDiscovery()
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

        stopBluetoothScanPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                bluetoothAdapter.adapter.cancelDiscovery()
                //unregisterReceiver(receiver)
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initViews() {
        enableButton = findViewById<Button>(R.id.enable_button)
        disableButton = findViewById<Button>(R.id.disable_button)
        deviceListview = findViewById(R.id.device_list)
        bluetoothStatus = findViewById(R.id.bluetooth_status)
        progressBar = findViewById(R.id.progress)
        progressBar.isVisible = false
        deviceAdapter = GroupAdapter<GroupieViewHolder>()
        deviceAdapter.apply {
            setOnItemClickListener(onItemClickListener)
        }
        deviceListview.adapter = deviceAdapter
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.adapter?.bondedDevices
        pairedDevices?.forEach { device ->
            deviceAdapter.add(DeviceAdapter(device))
            deviceNameList.add(device)

        }

        deviceAdapter.notifyDataSetChanged()

    }

    @SuppressLint("MissingPermission")
    private val onItemClickListener = OnItemClickListener { item, _ ->
        print("test")
        if (item is DeviceAdapter) {
            val device = item.device!!
//            val connectThread = ConnectThread(device)
//            connectThread.start()

        }
    }

    @SuppressLint("MissingPermission")
    fun enableBluetooth() {
        //cyanAdapter.bluetoothAdapter.enable()

        //startBluetoothScanPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)

        bluetoothAdapter.adapter.startDiscovery()


    }


    @SuppressLint("MissingPermission")
    fun disableBluetooth() {
        bluetoothAdapter.adapter.cancelDiscovery()
        disableButton.isEnabled = false
        enableButton.isEnabled = true
    }


    private val receiver = object : BroadcastReceiver() {


        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action!!) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    var deviceName = device?.name.toString()
                    if (deviceName.isNullOrBlank()) {
                        deviceName = device?.address.toString()
                    }

                    val deviceHardwareAddress = device?.address
                    //device?.let { deviceNameList.add(it) }
//                    deviceAdapter.add(DeviceAdapter(device))
                    device?.let { viewModel.addDevice(it) }
                    println("Device info is ${deviceName} ${deviceHardwareAddress}")// MAC address
                }
                ACTION_DISCOVERY_FINISHED -> {
                    progressBar.isVisible = false
                    enableButton.isEnabled = true
                    disableButton.isEnabled = false
                }
                ACTION_DISCOVERY_STARTED -> {
                    progressBar.isVisible = true
                    enableButton.isEnabled = false
                    disableButton.isEnabled = true
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.adapter?.listenUsingInsecureRfcommWithServiceRecord(
                "test",
                UUID.fromString("9b05086f-543e-404d-93bf-9f32492f5ffd")
            )
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    //manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString("9b05086f-543e-404d-93bf-9f32492f5ffd"))
        }

        @SuppressLint("MissingPermission")
        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.adapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                //manageMyConnectedSocket(socket)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
}


