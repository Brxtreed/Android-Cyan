package com.android.cyan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat.startActivityForResult

import android.bluetooth.BluetoothAdapter

import android.content.Intent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect


@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    val cyanAdapter = CyanAdapter()
    lateinit var bluetoothStatus: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enableBluetooth()
        setContentView(R.layout.activity_main)
        var button = findViewById<Button>(R.id.button)
        var disableButton = findViewById<Button>(R.id.disable_button)
        bluetoothStatus = findViewById(R.id.bluetooth_status)

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
        cyanAdapter.bluetoothAdapter.enable()

    }

    fun disableBluetooth(){
        cyanAdapter.bluetoothAdapter.disable()

    }
}