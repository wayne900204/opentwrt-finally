package com.example.openwrtmanager.ui.device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database.DeviceItem
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository.DeviceItemRepository
import kotlinx.coroutines.launch

class DeviceViewModel(
    private val repository: DeviceItemRepository,
) : ViewModel() {

    private val TAG = DeviceViewModel::class.qualifiedName
    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text
    val todoLiveData: LiveData<List<DeviceItem>> = repository.getDeviceItems()

    fun createDeviceItem(deviceItem: DeviceItem) {

        viewModelScope.launch {
            repository.insertDeviceItem(deviceItem)
        }
    }

    fun deleteDeviceItemByID(id: Int) {
        viewModelScope.launch {
            repository.deleteDeviceItemById(id)
        }
    }

    fun getDeviceItemByID(id: Int): LiveData<DeviceItem> {
        return repository.getDeviceItemById(id)
    }

    fun updateDeviceItemById(
        display: String,
        address: String,
        port: String,
        username: String,
        password: String,
        useHttpsConnection: Boolean,
        ignoreBadCertificate: Boolean,
        id: Int
    ) {
        viewModelScope.launch {
            repository.updateDeviceItemById(
                display,
                address,
                port,
                username,password,
                useHttpsConnection,
                ignoreBadCertificate,
                id
            )
        }
    }
}
