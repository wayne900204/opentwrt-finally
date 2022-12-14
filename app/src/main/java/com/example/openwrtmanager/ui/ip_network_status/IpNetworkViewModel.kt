package com.example.openwrtmanager.ui.ip_network_status

import android.util.Log
import androidx.lifecycle.*
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository.AuthenticateRepository
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository.DeviceItemRepository
import com.example.openwrtmanager.com.example.openwrtmanager.ui.identity.model.InfoRequestModel
import com.example.openwrtmanager.com.example.openwrtmanager.ui.identity.repository.InfoRepository
import com.example.openwrtmanager.com.example.openwrtmanager.ui.identity.repository.interval
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import com.example.openwrtmanager.com.example.openwrtmanager.utils.Utils
import com.example.openwrtmanager.ui.information.InfoViewModel
import com.example.openwrtmanager.ui.information.LCE
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class IpNetworkViewModel(
    private var infoRepository: InfoRepository,
    private var deviceItemRepo: DeviceItemRepository,
    private var authenticateRepo: AuthenticateRepository
) : ViewModel() {
    // TODO: Implement the ViewModel

    private val TAG = InfoViewModel::class.qualifiedName
    private val _startOrStopFlow = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    private val _lceLiveData = MutableLiveData<LCE<List<InfoResponseModelItem>>>(LCE.Loading)

    val lceLiveData: LiveData<LCE<List<InfoResponseModelItem>>> get() = _lceLiveData

    private var  authIsCorrect:Boolean = false
    private var cookie = ""
    private var domain = ""

    fun authenticate(deviceId: Int) = liveData(Dispatchers.IO) {
        emit(LCE.Loading)
        try {
            val username = deviceItemRepo.suspendGetDeviceItemById(deviceId).username
            val password = deviceItemRepo.suspendGetDeviceItemById(deviceId).password
            domain = Utils.coverUrl(
                deviceItemRepo.suspendGetDeviceItemById(deviceId).port,
                deviceItemRepo.suspendGetDeviceItemById(deviceId).address,
                deviceItemRepo.suspendGetDeviceItemById(deviceId).useHttpsConnection
            )
            val response = authenticateRepo.authenticate(username, password, domain)
            Log.d(TAG, "authenticate: "+"?????????????????????"+response)
            if (response.code() == 302|| response.message() == "Found") {
                val cookieList = response.headers().values("Set-Cookie")
                val cookieId = cookieList[0].split(";").toTypedArray()[0]
                val cookieName = cookieId.split("=").toTypedArray()[0]
                if (cookieName.equals("sysauth")|| cookieName.equals("sysauth_http")) {
                    Log.d(TAG, "authenticate: "+"YESSS")
                    cookie = cookieId.split("=").toTypedArray()[1]
                    authIsCorrect = true
                    emit(LCE.Content(cookieId.split("=").toTypedArray()[1]))
                    return@liveData
                }
            }
            emit(LCE.Error(throwable = Throwable("Forbidden !")))
        } catch (exception: Exception) {
            emit(LCE.Error(throwable = exception))
        }
    }

    fun init() {
        val requestBodyJson = listOf(
            InfoRequestModel(id = 5, params = listOf<Any>(cookie, "network.interface", "dump", JsonObject())),
        )

        val intervalFlow = interval(initialDelayMillis = 0, periodMillis = 5_000)
            .flatMapLatest {
                flow {
                    if(authIsCorrect){
                        val response = infoRepository.getInformation(domain,requestBodyJson)
                        if (!response[0].result.isNullOrEmpty()) {
                            emit(LCE.Content(infoRepository.getInformation(domain,requestBodyJson)))
                        } else {
                            emit(LCE.Error(Throwable("WRONG COOKIE")))
                        }
                    }else{
                        emit(LCE.Error(Throwable("WRONG COOKIE")))
                    }
                }
                    .onStart {
                        emit(LCE.Loading) }
                    .catch {
                        emit(LCE.Error(Throwable("WRONG COOKIE")))
                    }
            }

        _startOrStopFlow.distinctUntilChanged()
            .flatMapLatest { start ->
                if (start) intervalFlow
                else {
                    emptyFlow()}
            }
            .onEach { _lceLiveData.value = it }
            .launchIn(viewModelScope)
    }

    fun start() {
        viewModelScope.launch { _startOrStopFlow.emit(true) }
    }

    fun stop() {
        viewModelScope.launch { _startOrStopFlow.emit(false) }
    }

}