package viewModels

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import util.ResponseHolder

class CyanViewModel(application: Application): AndroidViewModel(application) {
    val deviceList = MutableLiveData<ArrayList<BluetoothDevice>>()

    init{
        deviceList.value = ArrayList()
    }


    val device: MutableLiveData<ResponseHolder<BluetoothDevice>> by lazy {
        MutableLiveData<ResponseHolder<BluetoothDevice>>()
    }
    val selectedDevice: MutableLiveData<BluetoothDevice> by lazy {
        MutableLiveData<BluetoothDevice>()
    }


    fun addDevice(bluetoothDevice: BluetoothDevice) {
        val devices = deviceList.value
        deviceList.value?.add(bluetoothDevice)

        val responseHolder = ResponseHolder<BluetoothDevice>()
        responseHolder.data = bluetoothDevice
        device.postValue(responseHolder)
        //device.postValue(device)

    }


    init {

    }
    //val CyanAdapter = CyanAdapter()

    //val liveData = LiveData<Boolean>()
}