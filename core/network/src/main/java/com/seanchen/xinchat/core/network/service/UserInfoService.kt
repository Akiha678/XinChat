package com.seanchen.xinchat.core.network.service

import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserInfoService {
    @POST("user/info/updatePerson")
    suspend fun updatePersonInfo(@Body params: Map<String, Any>): NetworkResponse<Any>

    @POST("user/info/updatePassword")
    suspend fun updatePassword(@Body params: Map<String, String>): NetworkResponse<Any>

    @POST("user/info/logoff")
    suspend fun logoff(@Body params: Map<String, Any>): NetworkResponse<Any>

    @POST("user/info/bindPhone")
    suspend fun bindPhone(@Body params: Map<String, String>): NetworkResponse<Any>

    @GET("user/info/person")
    suspend fun getPersonInfo(): NetworkResponse<User>
}