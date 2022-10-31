package fragments

import DeviceAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.android.cyan.R
import com.android.cyan.databinding.FragmentDeviceListBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import viewModels.CyanViewModel

class DeviceListFragment : Fragment() {
    private var _binding: FragmentDeviceListBinding? = null
    private val binding get() = _binding!!
    private lateinit var deviceAdapter: GroupAdapter<GroupieViewHolder>
    private lateinit var viewModel: CyanViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel= ViewModelProvider(requireActivity()).get(CyanViewModel::class.java)
        _binding = FragmentDeviceListBinding.inflate(inflater, container, false)
        val view = binding.root
        initData()
        initObservers()
        return view
    }

    private fun initObservers() {
        viewModel.device.observe(viewLifecycleOwner) { device ->
            deviceAdapter.add(DeviceAdapter(device.data))
            device.responseCode
        }
    }

    fun initData(){
        val devices = viewModel.deviceList.value
        deviceAdapter = GroupAdapter<GroupieViewHolder>()
        deviceAdapter.apply {
           // setOnItemClickListener(onItemClickListener)
            setOnItemClickListener {item, _ ->
            if (item is DeviceAdapter) {
                viewModel.selectedDevice.value = item.device
                view?.findNavController()?.navigate(R.id.navigation_device_info)
            }

            }
        }
        binding.deviceListview.adapter = deviceAdapter

        if (devices != null) {
            for(device in devices){
                deviceAdapter.add(DeviceAdapter(device))

            }
        }
        //deviceAdapter.notifyDataSetChanged()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}