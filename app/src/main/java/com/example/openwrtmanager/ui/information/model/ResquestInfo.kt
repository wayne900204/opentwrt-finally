package com.example.openwrtmanager.com.example.openwrtmanager.ui.identity.model

data class InfoRequestModel (
    val jsonrpc: String = "2.0",
    val id: Int,
    val method: String = "call",
    val params: List<Any>
)


