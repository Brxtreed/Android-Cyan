import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.util.Log
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class BluetoothServerThread(bluetoothAdapter: BluetoothManager) : Thread() {

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
                Log.e(ContentValues.TAG, "Socket's accept() method failed", e)
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
            Log.e(ContentValues.TAG, "Could not close the connect socket", e)
        }
    }
}