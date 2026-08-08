package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.datastore.datasource.auth.AuthStoreDataSource
import com.seanchen.xinchat.core.model.entity.Auth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStoreRepository @Inject constructor(
    private val authStoreDataSource: AuthStoreDataSource
){
    /**
     * 保存认证信息到本地
     */
    suspend fun saveAuth(auth: Auth) {
        authStoreDataSource.saveAuth(auth)
    }

    /**
     * 从本地获取认证信息
     */
    suspend fun getAuth(): Auth? {
        return authStoreDataSource.getAuth()
    }

    /**
     * 从本地获取Token
     */
    suspend fun getToken(): String? {
        return authStoreDataSource.getToken()
    }

    /**
     * 清除本地认证信息
     */
    suspend fun clearAuth() {
        authStoreDataSource.clearAuth()
    }

    /**
     * 检查用户是否已登录
     */
    suspend fun isLoggedIn(): Boolean {
        return authStoreDataSource.isLoggedIn()
    }

    /**
     * 检查Token是否需要刷新
     */
    suspend fun shouldRefreshToken(): Boolean {
        val auth = authStoreDataSource.getAuth() ?: return false
        return auth.shouldRefresh()
    }
}