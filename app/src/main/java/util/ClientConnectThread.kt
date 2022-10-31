package util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.util.Log
import java.io.IOException
import java.util.*

//Called whenever a client wishes to connect with a server
@SuppressLint("MissingPermission")
class ClientConnectThread(device: BluetoothDevice, private val bluetoothAdapter: BluetoothManager) : Thread() {

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
            Log.e(ContentValues.TAG, "Could not close the client socket", e)
        }
    }
}