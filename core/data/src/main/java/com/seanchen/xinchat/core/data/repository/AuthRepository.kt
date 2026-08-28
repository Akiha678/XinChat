package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.response.LoginResponse
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.datadource.auth.AuthNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val authNetworkDataSource: AuthNetworkDataSource
) {
    /**
     * 用户注册
     */
    suspend fun register(params: Map<String, String>): LoginResponse {
        return authNetworkDataSource.register(params)
    }

    /**
     * 获取注册验证码
     */
    fun getRegisterCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getRegisterCode(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 获取修改密码验证码
     */
    fun getPasswordCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getPasswordCode(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 修改密码
     */
    fun updatePassword(params: Map<String, String>): Flow<NetworkResponse<Boolean>> = flow {
        emit(authNetworkDataSource.updatePassword(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 手机号登录
     */
    fun loginByPhone(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.loginByPhone(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 密码登录
     */
    fun loginByPassword(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.loginByPassword(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 校验图形验证码
     */
    fun verifyCaptcha(params: Map<String, String>): Flow<NetworkResponse<Boolean>> = flow {
        emit(authNetworkDataSource.verifyCaptcha(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 获取图片验证码
     */
    fun getCaptcha(): Flow<NetworkResponse<Captcha>> = flow {
        emit(authNetworkDataSource.getCaptcha())
    }.flowOn(Dispatchers.IO)

    /**
     * 刷新Token
     */
    fun refreshToken(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.refreshToken(params))
    }.flowOn(Dispatchers.IO)
}
