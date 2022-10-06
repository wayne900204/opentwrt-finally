package com.example.openwrtmanager.com.example.openwrtmanager.ui.device.repository

import com.example.openwrtmanager.com.example.openwrtmanager.ui.device.network.ApiService

class AuthenticateRepository(private val apiHelper: ApiService) {
    suspend fun authenticate(username: String, password: String, domain: String) = apiHelper.authenticate(username, password,domain+"cgi-bin/luci/")
}