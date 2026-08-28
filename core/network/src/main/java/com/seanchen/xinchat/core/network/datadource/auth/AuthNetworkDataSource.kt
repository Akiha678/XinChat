package com.seanchen.xinchat.core.network.datadource.auth

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.response.LoginResponse
import com.seanchen.xinchat.core.model.response.NetworkResponse

interface AuthNetworkDataSource {
    /**
     * 用户注册
     */
    suspend fun register(params: Map<String, String>): LoginResponse

    /**
     * 获取注册验证码
     */
    suspend fun getRegisterCode(params: Map<String, String>): NetworkResponse<String>

    /**
     * 获取修改密码验证码
     */
    suspend fun getPasswordCode(params: Map<String, String>): NetworkResponse<String>

    /**
     * 修改密码
     */
    suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Boolean>

    /**
     * 手机号登录
     */
    suspend fun loginByPhone(params: Map<String, String>): NetworkResponse<Auth>

    /**
     * 密码登录
     */
    suspend fun loginByPassword(params: Map<String, String>): NetworkResponse<Auth>

    /**
     * 校验图形验证码
     */
    suspend fun verifyCaptcha(params: Map<String, String>): NetworkResponse<Boolean>

    /**
     * 获取图片验证码
     */
    suspend fun getCaptcha(): NetworkResponse<Captcha>

    /**
     * 刷新token
     */
    suspend fun refreshToken(params: Map<String, String>): NetworkResponse<Auth>
}
