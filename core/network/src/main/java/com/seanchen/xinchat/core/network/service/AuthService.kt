package com.seanchen.xinchat.core.network.service

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.response.LoginResponse
import com.seanchen.xinchat.core.model.response.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    /**
     * 注册
     */
    @POST("auth/register")
    suspend fun register(
        @Body params: Map<String, String>
    ): LoginResponse

    /**
     * 获取注册验证码
     */
    @POST("auth/register/code")
    suspend fun getRegisterCode(
        @Body params: Map<String, String>
    ): NetworkResponse<String>

    /**
     * 获取修改密码验证码
     */
    @POST("auth/password/code")
    suspend fun getPasswordCode(
        @Body params: Map<String, String>
    ): NetworkResponse<String>

    /**
     * 修改密码
     */
    @POST("auth/updatePassword")
    suspend fun updatePassword(
        @Body params: Map<String, String>
    ): NetworkResponse<Boolean>

    /**
     * 手机号登录
     */
    @POST("auth/login/phone")
    suspend fun loginByPhone(
        @Body params: Map<String, String>
    ): NetworkResponse<Auth>

    /**
     * 密码登录
     */
    @POST("auth/login/password")
    suspend fun loginByPassword(
        @Body params: Map<String, String>
    ): NetworkResponse<Auth>

    /**
     * 验证图形验证码
     */
    @POST("auth/login/captcha/verify")
    suspend fun verifyCaptcha(
        @Body params: Map<String, String>
    ): NetworkResponse<Boolean>

    /**
     * 获取图片验证码
     */
    @GET("auth/login/captcha")
    suspend fun getCaptcha(): NetworkResponse<Captcha>

    /**
     * 刷新Token
     */
    @POST("auth/login/refreshToken")
    suspend fun refreshToken(
        @Body params: Map<String, String>
    ): NetworkResponse<Auth>
}
