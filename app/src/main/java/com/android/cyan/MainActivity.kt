package com.android.cyan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context

import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import androidx.core.app.ActivityCompat

import android.location.LocationManager
import android.provider.Settings
import android.widget.*


val REQUEST_COARSE_LOCATION = 101


@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    val cyanAdapter = CyanAdapter()
    var deviceNameList = mutableListOf<String>()
    lateinit var deviceAdapter: ArrayAdapter<String>
    lateinit var bluetoothStatus: TextView
    lateinit var deviceListview: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNameList)

        val permissions = arrayOf(android.Manifest.permission.BLUETOOTH)
        ActivityCompat.requestPermissions(this, permissions,0)
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                2
            )
        }
        //enableBluetooth()
        setContentView(R.layout.activity_main)
        var button = findViewById<Button>(R.id.button)
        var disableButton = findViewById<Button>(R.id.disable_button)
        deviceListview = findViewById(R.id.device_list)
        bluetoothStatus = findViewById(R.id.bluetooth_status)
        //deviceListview.adapter = deviceAdapter

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED)
        registerReceiver(receiver, filter)

        button.setOnClickListener {
            //Toast.makeText(this, "Bluetooth is ${cyanAdapter.getBlueTooth().isEnabled}", Toast.LENGTH_LONG).show()
          enableBluetooth()




        }

        disableButton.setOnClickListener{
            disableBluetooth()

        }

        lifecycleScope.launch {
            cyanAdapter.bluetoothStatus
                .collect {
                    bluetoothStatus.text = it.toString()
                    // New location! Update the map
                }
        }
    }

    fun enableBluetooth(){


        //cyanAdapter.bluetoothAdapter.enable()

        cyanAdapter.bluetoothAdapter.startDiscovery()

    }

    fun disableBluetooth(){
        cyanAdapter.bluetoothAdapter.disable()
        //unregisterReceiver(receiver)

    }



    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action!!
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name.toString()
                    val deviceHardwareAddress = device?.address
                    deviceNameList.add(deviceName)
                    deviceAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceNameList)
                    deviceListview.adapter = deviceAdapter
                println("Device info is ${deviceName} ${deviceHardwareAddress}")// MAC address
                }
            }
        }
    }
}