package com.seanchen.xinchat.core.datastore.datasource.userinfo

import com.seanchen.xinchat.core.model.entity.User

interface UserInfoStoreDataSource {
    /**
     * 保存用户信息
     */
    suspend fun saveUserInfo(user: User)

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): User?

    /**
     * 更新用户信息
     */
    suspend fun updateUserInfo(updates: Map<String, Any?>)

    /**
     * 清除用户信息
     */
    suspend fun clearUserInfo()

    suspend fun getUserId(): Long

    suspend fun getNickName(): String?

    suspend fun getAvatarUrl(): String?
}