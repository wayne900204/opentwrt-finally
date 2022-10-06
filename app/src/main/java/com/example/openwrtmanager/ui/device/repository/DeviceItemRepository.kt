package com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository

import androidx.lifecycle.LiveData
import com.example.openwrtmanager.com.example.openwrtmanager.AppDatabase
import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database.DeviceItem

class DeviceItemRepository(
    private val database: AppDatabase
) {
    suspend fun insertDeviceItem(deviceItem: DeviceItem) {
        database.deviceItemDao().insert(deviceItem)
    }

    suspend fun updateDeviceItem(deviceItem: DeviceItem) {
        database.deviceItemDao().update(deviceItem)
    }

    fun getDeviceItems(): LiveData<List<DeviceItem>> {
        return database.deviceItemDao().findAll()
    }

    suspend fun deleteDeviceItemById(id: Int) {
        database.deviceItemDao().deleteById(id)
    }

    fun getDeviceItemById(id: Int): LiveData<DeviceItem> {
        return database.deviceItemDao().getById(id)
    }

    suspend fun suspendGetDeviceItemById(id: Int): DeviceItem {
        return database.deviceItemDao().suspendGetDeviceDeviceItemById(id)
    }

    suspend fun updateDeviceItemById(
        displayName: String,
        address: String,
        port: String,
        username: String,
        password: String,
        useHttpsConnection: Boolean,
        ignoreBadCertificate: Boolean,
        id: Int
    ) {
        database.deviceItemDao().updateById(
            displayName,
            address,
            port,
            username,password,
            useHttpsConnection,
            ignoreBadCertificate,
            id
        )
    }
}
