package com.seanchen.xinchat.core.network.datadource.auth

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.response.LoginResponse
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.base.BaseNetworkDataSource
import com.seanchen.xinchat.core.network.service.AuthService
import javax.inject.Inject

class AuthNetworkDataSourceImpl @Inject constructor(
    private val authService: AuthService
) : BaseNetworkDataSource(), AuthNetworkDataSource {
    /**
     * 注册
     */
    override suspend fun register(params: Map<String, String>): LoginResponse {
        return authService.register(params)
    }

    override suspend fun getRegisterCode(params: Map<String, String>): NetworkResponse<String> {
        return authService.getRegisterCode(params)
    }

    override suspend fun getPasswordCode(params: Map<String, String>): NetworkResponse<String> {
        return authService.getPasswordCode(params)
    }

    override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Boolean> {
        return authService.updatePassword(params)
    }

    override suspend fun loginByPhone(params: Map<String, String>): NetworkResponse<Auth> {
        return authService.loginByPhone(params)
    }

    override suspend fun loginByPassword(params: Map<String, String>): NetworkResponse<Auth> {
        return authService.loginByPassword(params)
    }

    override suspend fun verifyCaptcha(params: Map<String, String>): NetworkResponse<Boolean> {
        return authService.verifyCaptcha(params)
    }

    override suspend fun getCaptcha(): NetworkResponse<Captcha> {
        return authService.getCaptcha()
    }

    override suspend fun refreshToken(params: Map<String, String>): NetworkResponse<Auth> {
        return authService.refreshToken(params)
    }
}
