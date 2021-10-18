package com.android.cyan

import android.bluetooth.BluetoothAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class CyanAdapter {

    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    fun getBlueTooth(): BluetoothAdapter
    {
        return bluetoothAdapter
    }

    val bluetoothStatus: Flow<Boolean> = flow {
        while(true) {
            val isEnabled = bluetoothAdapter.isEnabled
            emit(isEnabled) // Emits the result of the request to the flow
            delay(100) // Suspends the coroutine for some time
        }
    }
}


