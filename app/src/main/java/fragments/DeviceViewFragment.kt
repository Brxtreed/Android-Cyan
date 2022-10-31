package fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.android.cyan.databinding.FragmentDeviceListBinding
import com.android.cyan.databinding.FragmentDeviceViewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import viewModels.CyanViewModel

class DeviceViewFragment : Fragment() {
    private var _binding: FragmentDeviceViewBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentDeviceViewBinding.inflate(inflater, container, false)
        val view = binding.root
        initData()
        initObservers()
        return view
    }

    private fun initObservers() {
        viewModel.device.observe(viewLifecycleOwner) { device ->

        }
    }

    @SuppressLint("MissingPermission")
    private fun initData(){
        val device = viewModel.selectedDevice.value
        binding.deviceName.text = device?.name
        binding.deviceMacAddressText.text = device?.address
    }

}