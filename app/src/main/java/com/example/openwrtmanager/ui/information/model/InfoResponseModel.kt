package com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model
import com.google.gson.annotations.SerializedName

data class InfoResponseModelItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("jsonrpc")
    val jsonrpc: String,
    @SerializedName("result")
    val result: List<Any>
)