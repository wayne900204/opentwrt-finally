package com.example.openwrtmanager.com.example.openwrtmanager.ui.wifi_device

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openwrtmanager.com.example.openwrtmanager.AppDatabase
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.network.ApiClient
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.network.ApiService
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.network.InfoService
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository.AuthenticateRepository
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository.DeviceItemRepository
import com.example.openwrtmanager.com.example.openwrtmanager.ui.identity.network.InfoClient
import com.example.openwrtmanager.com.example.openwrtmanager.ui.identity.repository.InfoRepository
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.com.example.openwrtmanager.ui.wifi_device.adapter.WifiDeviceAdapter
import com.example.openwrtmanager.com.example.openwrtmanager.utils.AnyViewModelFactory
import com.example.openwrtmanager.com.example.openwrtmanager.utils.LoadingDialog
import com.example.openwrtmanager.com.example.openwrtmanager.utils.MyLogger
import com.example.openwrtmanager.databinding.FragmentWifiDeviceBinding
import com.example.openwrtmanager.ui.information.LCE
import kotlinx.coroutines.*
import java.lang.Runnable

class WifiDeviceFragment : Fragment() {


    private val TAG = WifiDeviceFragment::class.simpleName
    private var _binding: FragmentWifiDeviceBinding? = null
    private val binding get() = _binding!!
    private lateinit var wifiDeviceViewModel: WifiDeviceViewModel

    private lateinit var viewModel: WifiDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWifiDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        val pref = requireActivity().getSharedPreferences("OpenWrt", Context.MODE_PRIVATE)
        val loadingDialog = LoadingDialog(requireActivity())
        val adapter = WifiDeviceAdapter()
        binding.wifiDeviceRecyclerView.adapter = adapter
        binding.wifiDeviceRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.wifiDeviceRecyclerView.addItemDecoration(
            DividerItemDecoration(
                binding.root.context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.wifiDeviceRecyclerView.itemAnimator = null
        adapter.submitList(mutableListOf())
        val handler = Handler()
        // 1. 先取得 device select item 的 id，拿到帳號密碼去驗證，取道 cookie
        // 2. 拿去 cookie 去請求資料
        wifiDeviceViewModel.authenticate(pref.getInt("device_select_item_id", -1))
            .observe(viewLifecycleOwner,
                Observer { lce ->
                    when (lce) {
                        is LCE.Loading -> {
                            loadingDialog.startLoadingDialog()
                        }
                        is LCE.Content -> {
                            runBlocking {
                                GlobalScope.launch(Dispatchers.IO) {
                                    handler.postDelayed(
                                        Runnable { loadingDialog.dismissDialog() },
                                        500
                                    )
                                    delay(500)
                                }.join()
                            }
                            MyLogger.i(TAG, "COOKIE：" + lce.content)
                            wifiDeviceViewModel.init()
                            wifiDeviceViewModel.start()
                            wifiDeviceViewModel.lceLiveData.observe(
                                viewLifecycleOwner,
                                Observer { lce ->
                                    when (lce) {
                                        is LCE.Content -> {
                                            val recyclerViewState =
                                                binding.wifiDeviceRecyclerView.layoutManager?.onSaveInstanceState()
                                            val newitem =
                                                mutableListOf<MutableList<InfoResponseModelItem>>(
                                                    mutableListOf<InfoResponseModelItem>(
                                                        lce.content[0],
                                                        lce.content[1],
                                                        lce.content[2]
                                                    ),                // position = 1
                                                )
                                            adapter.submitList(newitem) {
                                                binding.wifiDeviceRecyclerView.layoutManager?.onRestoreInstanceState(
                                                    recyclerViewState
                                                )
                                            }
                                        }
                                        is LCE.Error -> Toast.makeText(
                                            requireContext(),
                                            (lce.throwable.toString()),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        is LCE.Loading -> {
//                                            Toast.makeText(requireContext(), ("別搞我"), Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                        }
                                    }
                                })
                        }
                        is LCE.Error -> {
                            runBlocking {
                                GlobalScope.launch(Dispatchers.IO) {
                                    handler.postDelayed(
                                        Runnable { loadingDialog.dismissDialog() },
                                        500
                                    )
                                    delay(500)
                                }.join()
                            }
                            Toast.makeText(
                                requireContext(),
                                lce.throwable.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {}
                    }
                })
    }

    private fun setUpViewModel() {
        val infoRep = InfoRepository(InfoClient.getInfoRetrofit().create(InfoService::class.java))
        val todoItemDb = AppDatabase.getInstance(requireActivity().applicationContext)
        val deviceItemRepo = DeviceItemRepository(todoItemDb)
        val authenticateRepo =
            AuthenticateRepository(ApiClient.getRetrofit().create(ApiService::class.java))
        val test = AnyViewModelFactory {
            WifiDeviceViewModel(
                infoRep,
                deviceItemRepo,
                authenticateRepo
            )
        }
        wifiDeviceViewModel =
            ViewModelProvider(requireActivity(), test).get(WifiDeviceViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        requireActivity().viewModelStore.clear()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        wifiDeviceViewModel.start()
    }

    override fun onPause() {
        super.onPause()
        wifiDeviceViewModel.stop()
    }
}