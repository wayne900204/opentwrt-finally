package com.example.openwrtmanager.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openwrtmanager.com.example.openwrtmanager.AppDatabase
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.DeviceAdapter
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database.DeviceItem
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository.DeviceItemRepository
import com.example.openwrtmanager.com.example.openwrtmanager.utils.AnyViewModelFactory
import com.example.openwrtmanager.databinding.FragmentDeviceBinding

class DeviceFragment : Fragment() {

    private val TAG = DeviceFragment::class.qualifiedName
    private var _binding: FragmentDeviceBinding? = null
    private val binding get() = _binding!!

    private lateinit var deviceViewModel: DeviceViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todoItemDb = AppDatabase.getInstance(requireActivity().applicationContext)
        val deviceItemRepo = DeviceItemRepository(todoItemDb)
        val viewModelFactory = AnyViewModelFactory {
            DeviceViewModel(deviceItemRepo)
        }
        deviceViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            DeviceViewModel::class.java
        )

        val adapter = DeviceAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        deviceViewModel.todoLiveData.observe(
            viewLifecycleOwner,
            Observer { todos: List<DeviceItem> -> adapter.updateAdapterData(todos) })

        binding.addRouter.setOnClickListener {
            findNavController().navigate(DeviceFragmentDirections.actionDeviceFragmentToAddDeviceFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
