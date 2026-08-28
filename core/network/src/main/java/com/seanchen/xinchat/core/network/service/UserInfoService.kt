package com.seanchen.xinchat.core.network.service

import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserInfoService {
    @POST("chat/updatePersonInfo")
    suspend fun updatePersonInfo(@Body params: Map<String, Any>): NetworkResponse<Any>

    @POST("chat/updatePassword")
    suspend fun updatePassword(@Body params: Map<String, String>): NetworkResponse<Any>

    @POST("chat/logoff")
    suspend fun logoff(@Body params: Map<String, Any>): NetworkResponse<Boolean>

    @POST("chat/bindPhone")
    suspend fun bindPhone(@Body params: Map<String, String>): NetworkResponse<Any>

    @GET("chat/person")
    suspend fun getPersonInfo(): NetworkResponse<User>
}
