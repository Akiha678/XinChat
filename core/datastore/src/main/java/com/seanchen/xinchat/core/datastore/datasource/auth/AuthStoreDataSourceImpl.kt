package com.seanchen.xinchat.core.datastore.datasource.auth

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.util.storage.MMKVUtils
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AuthStoreDataSourceImpl @Inject constructor() : AuthStoreDataSource {
    companion object {
        private const val KEY_AUTH = "auth_info"
    }

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 保存认证信息
     */
    override suspend fun saveAuth(auth: Auth) {
        val authJson = json.encodeToString(auth)
        MMKVUtils.putString(KEY_AUTH, authJson)
    }

    /**
     * 获取认证信息
     */
    override suspend fun getAuth(): Auth? {
        val authJson = MMKVUtils.getString(KEY_AUTH, "")
        if (authJson.isEmpty()) return null

        return try {
            json.decodeFromString<Auth>(authJson)
        } catch (e: Exception){
            null
        }
    }

    /**
     * 获取用户 token
     */
    override suspend fun getToken(): String? {
        return getAuth()?.token
    }

    /**
     * 清除认证信息
     */
    override suspend fun clearAuth() {
        MMKVUtils.remove(KEY_AUTH)
    }

    /**
     * 检查是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        val auth = getAuth() ?: return false
        return !auth.isExpired() && auth.token.isNotEmpty()
    }
}