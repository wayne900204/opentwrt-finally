package com.example.openwrtmanager.com.example.openwrtmanager.ui.device

import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.database.DeviceItem


interface DeviceUI {
    fun onDeviceItemChange(items: DeviceItem)
}
