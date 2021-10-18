package com.android.cyan

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat.startActivityForResult

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context

import android.content.Intent
import android.content.IntentFilter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat.startActivityForResult

import android.location.LocationManager
import android.provider.Settings


val REQUEST_COARSE_LOCATION = 101


@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    val cyanAdapter = CyanAdapter()
    lateinit var bluetoothStatus: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        bluetoothStatus = findViewById(R.id.bluetooth_status)

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
            cyanAdapter.latestNews
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
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address
                println("Device info is ${deviceName} ${deviceHardwareAddress}")// MAC address
                }
            }
        }
    }
}