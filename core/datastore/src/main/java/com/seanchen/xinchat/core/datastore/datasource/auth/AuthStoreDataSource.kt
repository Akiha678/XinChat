package com.seanchen.xinchat.core.datastore.datasource.auth

import com.seanchen.xinchat.core.model.entity.Auth

interface AuthStoreDataSource {
    /**
     * 保存认证信息
     */
    suspend fun saveAuth(auth: Auth)

    /**
     * 获取认证信息
     */
    suspend fun getAuth(): Auth?

    /**
     * 获取用户 token
     */
    suspend fun getToken(): String?

    /**
     * 清除认证信息
     */
    suspend fun clearAuth()

    /**
     * 检查是否已登录
     */
    suspend fun isLoggedIn(): Boolean
}