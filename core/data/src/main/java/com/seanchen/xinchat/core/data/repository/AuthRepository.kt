package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
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
    fun register(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.register(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 获取短信验证码
     */
    fun getSmsCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getSmsCode(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 刷新Token
     */
    fun refreshToken(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.refreshToken(params))
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
     * 获取图片验证码
     */
    fun getCaptcha(): Flow<NetworkResponse<Captcha>> = flow {
        emit(authNetworkDataSource.getCaptcha())
    }.flowOn(Dispatchers.IO)
}
