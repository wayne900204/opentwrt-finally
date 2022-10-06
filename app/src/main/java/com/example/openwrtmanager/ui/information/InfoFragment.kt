package com.example.openwrtmanager.ui.information

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.InfoAdapter
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.com.example.openwrtmanager.utils.AnyViewModelFactory
import com.example.openwrtmanager.com.example.openwrtmanager.utils.LoadingDialog
import com.example.openwrtmanager.com.example.openwrtmanager.utils.MyLogger
import com.example.openwrtmanager.databinding.FragmentInfoBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class InfoFragment : Fragment() {

    private val TAG = InfoFragment::class.simpleName
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var infoViewModel: InfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
        val pref = requireActivity().getSharedPreferences("OpenWrt", Context.MODE_PRIVATE)
        val loadingDialog = LoadingDialog(requireActivity())
        val adapter = InfoAdapter()
        binding.infoRecyclerView.adapter = adapter
        binding.infoRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.infoRecyclerView.addItemDecoration(DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL))
        binding.infoRecyclerView.itemAnimator = null
        adapter.submitList(mutableListOf())
        val handler = Handler()

        infoViewModel.authenticate(pref.getInt("device_select_item_id", -1))
            .observe(viewLifecycleOwner,
                Observer { lce ->
                    when (lce) {
                        is LCE.Loading -> {
                            loadingDialog.startLoadingDialog()
                        }
                        is LCE.Content -> {
                            runBlocking {
                                GlobalScope.launch(IO) {
                                    handler.postDelayed(
                                        Runnable { loadingDialog.dismissDialog() },
                                        500
                                    )
                                    delay(500)
                                }.join()
                            }
                            MyLogger.i(TAG, "COOKIE：" + lce.content)
                            infoViewModel.init()
                            infoViewModel.start()
                        }
                        is LCE.Error -> {
                            runBlocking {
                                GlobalScope.launch(IO) {
                                    handler.postDelayed(
                                        Runnable { loadingDialog.dismissDialog() },
                                        500
                                    )
                                    delay(500)
                                }.join()
                            }
                            Toast.makeText(requireContext(),lce.throwable.toString(), Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                })

        infoViewModel.lceLiveData.observe(viewLifecycleOwner, Observer { lce ->
            when (lce) {
                is LCE.Content -> {
                    val recyclerViewState = binding.infoRecyclerView.layoutManager?.onSaveInstanceState()
                    val newitem = mutableListOf<MutableList<InfoResponseModelItem>>(
                        mutableListOf<InfoResponseModelItem>(lce.content[0],lce.content[1]), // position = 0
                        mutableListOf<InfoResponseModelItem>(lce.content[4]),                // position = 1
                        mutableListOf<InfoResponseModelItem>(lce.content[3],lce.content[4]), // position = 2
//                        mutableListOf<InfoResponseModelItem>(lce.content[5],lce.content[6]),                // position = 3
//                        mutableListOf<InfoResponseModelItem>(lce.content[0],lce.content[1]), // position = 4
                        mutableListOf<InfoResponseModelItem>(lce.content[7]), // position = 5
                    )
                    adapter.submitList(newitem){
                        binding.infoRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                    }
                    }
                is LCE.Error -> Toast.makeText(
                    requireContext(),
                    (lce.throwable.toString()),
                    Toast.LENGTH_SHORT
                ).show()
                else -> {}
            }
        })
    }

    private fun setUpViewModel() {
        val infoRep = InfoRepository(InfoClient.getInfoRetrofit().create(InfoService::class.java))
        val todoItemDb = AppDatabase.getInstance(requireActivity().applicationContext)
        val deviceItemRepo = DeviceItemRepository(todoItemDb)
        val authenticateRepo = AuthenticateRepository(ApiClient.getRetrofit().create(ApiService::class.java))
        val test = AnyViewModelFactory { InfoViewModel(infoRep, deviceItemRepo, authenticateRepo) }
        infoViewModel = ViewModelProvider(requireActivity(), test).get(InfoViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        requireActivity().viewModelStore.clear()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        infoViewModel.start()
    }

    override fun onPause() {
        super.onPause()
        infoViewModel.stop()
    }
}


