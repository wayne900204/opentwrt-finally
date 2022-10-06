package com.example.openwrtmanager.com.example.openwrtmanager.ui.device.network

import com.example.openwrtmanager.com.example.openwrtmanager.ui.identity.model.InfoRequestModel
import com.example.openwrtmanager.com.example.openwrtmanager.ui.information.model.InfoResponseModelItem
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url


// Github Doc - https://developer.github.com/v3/

interface InfoService {
    @POST()
    suspend fun getInformation(@Url url:String, @Body requestBody: List<InfoRequestModel>): List<InfoResponseModelItem>
}