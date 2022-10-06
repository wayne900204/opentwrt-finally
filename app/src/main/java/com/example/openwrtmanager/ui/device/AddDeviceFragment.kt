package com.example.openwrtmanager.ui.device


import android.content.Context
import android.net.InetAddresses
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.openwrtmanager.com.example.openwrtmanager.AppDatabase
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.AddDeviceViewModel
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.DeviceUI
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database.DeviceItem
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.network.ApiClient
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.network.ApiService
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository.AuthenticateRepository
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository.DeviceItemRepository
import com.example.openwrtmanager.com.example.openwrtmanager.utils.AnyViewModelFactory
import com.example.openwrtmanager.com.example.openwrtmanager.utils.LoadingDialog
import com.example.openwrtmanager.com.example.openwrtmanager.utils.Status
import com.example.openwrtmanager.com.example.openwrtmanager.utils.isNetworkAvailable
import com.example.openwrtmanager.databinding.AddDeviceFragmentBinding
import java.util.*


class AddDeviceFragment : Fragment() {

    private val TAG = AddDeviceFragment::class.qualifiedName
    private var _binding: AddDeviceFragmentBinding? = null
    private val binding get() = _binding!!

    val args: AddDeviceFragmentArgs by navArgs()
    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var adddeviceViewModel: AddDeviceViewModel
    private var identitiyId: Int? = null
    var deviceUI: DeviceUI? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddDeviceFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        setInitData()
        setUiWithData()
        val loadingDialog = LoadingDialog(requireActivity())
        if (args.isEdit) {
            binding.delete.visibility = View.VISIBLE
        }
//        val filters = arrayOfNulls<InputFilter>(1)
//        filters[0] = InputFilter { source, start, end, dest, dstart, dend ->
//            if (end > start) {
//                val destTxt = dest.toString()
//                val resultingTxt = (destTxt.substring(0, dstart)
//                        + source.subSequence(start, end)
//                        + destTxt.substring(dend))
//                if (!resultingTxt
//                        .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?".toRegex())
//                ) {
//                    return@InputFilter ""
//                } else {
//                    val splits = resultingTxt.split("\\.").toTypedArray()
//                    for (i in splits.indices) {
//                        if (Integer.valueOf(splits[i]) > 255) {
//                            return@InputFilter ""
//                        }
//                    }
//                }
//            }
//            null
//        }
//        binding.ipAddressInput.setFilters(filters)

        saveBtnOnClick()
        deleteBtnOnClick()
        useHttpsConnectionOnClick()

        binding.test.setOnClickListener {
            if (inputFieldValidation()) {
                val username = binding.usernameInput.text.toString();
                val password = binding.passwordInput.text.toString();
                if (!binding.ipAddressInput.text.isNullOrEmpty() && !binding.displayInput.text.isNullOrEmpty() && !binding.username.text.isNullOrEmpty() && !binding.password.text.isNullOrEmpty()) {
                    if (activity?.baseContext?.let { isNetworkAvailable() }!!) {
                        adddeviceViewModel.authenticate(username, password, getUrl())
                            .observe(viewLifecycleOwner) {
                                it?.let { resource ->
                                    when (resource.status) {
                                        Status.LOADING -> {
                                            loadingDialog.startLoadingDialog()
                                        }
                                        Status.SUCCESS -> {
                                            loadingDialog.dismissDialog()
                                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                        Status.ERROR -> {
                                            loadingDialog.dismissDialog()
                                            Toast.makeText(
                                                context,
                                                resource.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    private fun getUrl(): String {
        var a = binding.portInput.text.toString()
        if (binding.portInput.text.toString() == "" || binding.portInput.text.toString()
                .toInt() <= 0
        ) {
            a = "80"
        }
        if (binding.useHttpsConnection.isChecked) {
            return "https://" + binding.ipAddressInput.text.toString() + ":" + a + "/"
        } else {
            return "http://" + binding.ipAddressInput.text.toString() + ":" + a + "/"
        }

    }

    private fun inputFieldValidation(): Boolean {
        var boo = true
        if (binding.displayInput.text.isNullOrEmpty()) {
            binding.displayError.visibility = View.VISIBLE
            boo = false
        } else {
            binding.displayError.visibility = View.GONE
        }
        if (binding.ipAddressInput.text.isNullOrEmpty()) {
            boo = false
            binding.ipAddressError.text = "IP Address is Missing"
            binding.ipAddressError.visibility = View.VISIBLE
        } else {
            if (!isIpValid(binding.ipAddressInput.text.toString())) {
                boo = false
                binding.ipAddressError.text = "IP FORMAT IS NOT CORRECT"
                binding.ipAddressError.visibility = View.VISIBLE
            } else {
                binding.ipAddressError.visibility = View.GONE
            }
        }
        if (binding.usernameInput.text.isNullOrEmpty()) {
            boo = false
            binding.usernameError.visibility = View.VISIBLE
        } else {
            binding.usernameError.visibility = View.GONE
        }
        if (binding.passwordInput.text.isNullOrEmpty()) {
            boo = false
            binding.passwordInputError.visibility = View.VISIBLE
        } else {
            binding.passwordInputError.visibility = View.GONE
        }
        return boo
    }

    private fun setUpViewModel() {
        val todoItemDb = AppDatabase.getInstance(requireActivity().applicationContext)
        val deviceItemRepo = DeviceItemRepository(todoItemDb)
        val viewModelFactory = AnyViewModelFactory { DeviceViewModel(deviceItemRepo) }
        val a = AuthenticateRepository(ApiClient.getRetrofit().create(ApiService::class.java))
        val test = AnyViewModelFactory { AddDeviceViewModel(a) }

        adddeviceViewModel =
            ViewModelProvider(requireActivity(), test).get(AddDeviceViewModel::class.java)

        deviceViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            DeviceViewModel::class.java
        )
    }

    private fun setInitData() {
        if (args.isEdit) {
            deviceViewModel.getDeviceItemByID(args.id).observe(viewLifecycleOwner, Observer { it ->
                deviceUI?.onDeviceItemChange(it)
            })
        }
    }

    private fun setUiWithData() {
        deviceUI = object : DeviceUI {
            override fun onDeviceItemChange(items: DeviceItem) {
                binding.displayInput.setText(items.displayName)
                binding.ipAddressInput.setText(items.address)
                binding.portInput.setText(items.port)
                binding.usernameInput.setText(items.username)
                binding.passwordInput.setText(items.password)
                binding.useHttpsConnection.isChecked = items.useHttpsConnection
                binding.ignoreBadCertificate.isChecked = items.ignoreBadCertificate
                if (binding.useHttpsConnection.isChecked)
                    binding.ignoreBadCertificate.visibility = View.VISIBLE

            }

        }
    }


    fun isIpValid(ip: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            InetAddresses.isNumericAddress(ip)
        } else {
            Patterns.IP_ADDRESS.matcher(ip).matches()
        }
    }

    private fun saveBtnOnClick() {

        binding.save.setOnClickListener {
            if (inputFieldValidation()) {
                if (!binding.displayInput.text.isNullOrEmpty() && !binding.ipAddressInput.text.isNullOrEmpty() && !binding.username.text.isNullOrEmpty() && !binding.password.text.isNullOrEmpty()) {
                    val display = binding.displayInput.text.toString()
                    val address = binding.ipAddressInput.text.toString()
                    val useHttpsConnection = binding.useHttpsConnection.isChecked
                    val port =
                        if (binding.portInput.text.isNullOrEmpty()) "80" else binding.portInput.text
                    val ignoreBadCertificate = binding.ignoreBadCertificate.isChecked
                    val username = binding.usernameInput.text.toString();
                    val password = binding.passwordInput.text.toString();
                    if (args.isEdit) {
                        deviceViewModel.updateDeviceItemById(
                            display,
                            address,
                            port.toString(),
                            username, password,
                            useHttpsConnection,
                            ignoreBadCertificate,
                            args.id
                        )
                    } else {
                        val item = DeviceItem(
                            displayName = display,
                            address = address,
                            username = username,
                            password = password,
                            useHttpsConnection = useHttpsConnection,
                            port = port.toString(),
                            ignoreBadCertificate = ignoreBadCertificate,
                            createdAt = Date()
                        )
                        deviceViewModel.createDeviceItem(item)
                    }
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun deleteBtnOnClick() {
        binding.delete.setOnClickListener {
            deviceViewModel.deleteDeviceItemByID(args.id)
            val pref = binding.root.context.getSharedPreferences("OpenWrt", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putInt("device_select_item_id", -1).apply()
            findNavController().popBackStack()
        }
    }

    private fun useHttpsConnectionOnClick() {
        binding.useHttpsConnection.setOnClickListener {
            if (binding.useHttpsConnection.isChecked) {
                binding.ignoreBadCertificate.visibility = View.VISIBLE
            } else {
                binding.ignoreBadCertificate.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
