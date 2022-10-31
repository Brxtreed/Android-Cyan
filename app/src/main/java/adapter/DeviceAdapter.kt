import android.bluetooth.BluetoothDevice
import android.view.View
import com.android.cyan.R
import com.android.cyan.databinding.CellDeviceBinding
import com.xwray.groupie.viewbinding.BindableItem


class DeviceAdapter(val device: BluetoothDevice?) : BindableItem<CellDeviceBinding>() {
    var onItemClick: ((BluetoothDevice) -> Unit)? = null

    override fun getLayout() = R.layout.cell_device

    override fun bind(viewBinding: CellDeviceBinding, position: Int) {
        viewBinding.deviceNameText.text = device?.alias.toString()
        viewBinding.deviceAddressText.text = device?.address
        viewBinding.deviceTypeText.text = device?.type.toString()


    }

    override fun initializeViewBinding(view: View): CellDeviceBinding = CellDeviceBinding.bind(view)
}