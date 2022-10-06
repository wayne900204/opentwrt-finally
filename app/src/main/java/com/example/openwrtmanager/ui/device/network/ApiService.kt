package com.example.openwrtmanager.com.example.openwrtmanager.ui.device.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url


// Github Doc - https://developer.github.com/v3/

interface ApiService {
    @FormUrlEncoded
    @POST()
    suspend fun authenticate(
        @Field("luci_username") username: String,
        @Field("luci_password") password: String,
        @Url() url:String
    ): Response<ResponseBody>
}