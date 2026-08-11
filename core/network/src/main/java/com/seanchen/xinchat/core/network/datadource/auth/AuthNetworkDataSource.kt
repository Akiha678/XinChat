package com.seanchen.xinchat.core.network.datadource.auth

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.response.NetworkResponse

interface AuthNetworkDataSource {
    /**
     * 用户注册
     */
    suspend fun register(params: Map<String, String>): NetworkResponse<Auth>

    /**
     * 获取短信验证码
     */
    suspend fun getSmsCode(params: Map<String, String>): NetworkResponse<String>

    /**
     * 刷新token
     */
    suspend fun refreshToken(params: Map<String, String>): NetworkResponse<Auth>

    /**
     * 手机号登录
     */
    suspend fun loginByPhone(params: Map<String, String>): NetworkResponse<Auth>

    /**
     * 密码登录
     */
    suspend fun loginByPassword(params: Map<String, String>): NetworkResponse<Auth>

    /**
     * 获取图片验证码
     */
    suspend fun getCaptcha(): NetworkResponse<Captcha>
}