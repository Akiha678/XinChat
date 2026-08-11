package com.seanchen.xinchat.core.network.service

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.response.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    /**
     * 注册
     */
    @POST("user/")
    suspend fun register(
        @Body params: Map<String, String>
    ): NetworkResponse<Auth>

    /**
     * 获取短信验证码
     */
    @POST("user/login/smsCode")
    suspend fun getSmsCode(
        @Body params: Map<String, String>
    ): NetworkResponse<String>

    /**
     * 刷新Token
     */
    @POST("user/login/refreshToken")
    suspend fun refreshToken(
        @Body params: Map<String, String>
    ): NetworkResponse<Auth>

    /**
     * 手机号登录
     */
    @POST("user/login/phone")
    suspend fun loginByPhone(
        @Body params: Map<String, String>
    ): NetworkResponse<Auth>

    /**
     * 密码登录
     */
    @POST("user/login/password")
    suspend fun loginByPassword(
        @Body params: Map<String, String>
    ): NetworkResponse<Auth>

    /**
     * 获取图片验证码
     */
    @GET("user/login/captcha")
    suspend fun getCaptcha(): NetworkResponse<Captcha>
}