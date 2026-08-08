package com.seanchen.xinchat.core.datastore.datasource.userinfo

import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.util.storage.MMKVUtils
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

class UserInfoStoreDataSourceImpl @Inject constructor() : UserInfoStoreDataSource{
    companion object {
        private const val KEY_USER_INFO = "user_info"
    }

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun saveUserInfo(user: User) {
        val userJson = json.encodeToString(user)
        MMKVUtils.putString(KEY_USER_INFO, userJson)
    }

    override suspend fun getUserInfo(): User? {
        val userJson = MMKVUtils.getString(KEY_USER_INFO, "")
        if (userJson.isEmpty()) return null

        return try {
            json.decodeFromString<User>(userJson)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUserInfo(updates: Map<String, Any?>) {
        val currentUser = getUserInfo() ?: return
        val userJson = MMKVUtils.getString(KEY_USER_INFO, "")
        if (userJson.isEmpty()) return

        try {
            // 解析当前用户JSON为可变映射
            val userObject = json.parseToJsonElement(userJson).jsonObject.toMutableMap()

            // 应用更新
            updates.forEach { (key, value) ->
                when (value) {
                    is String -> userObject[key] = JsonPrimitive(value)
                    is Number -> userObject[key] = JsonPrimitive(value)
                    is Boolean -> userObject[key] = JsonPrimitive(value)
                    null -> userObject.remove(key)
                }
            }

            // 保存更新后的JSON
            val updatedJson = JsonObject(userObject).toString()
            MMKVUtils.putString(KEY_USER_INFO, updatedJson)
        } catch (e: Exception) {
            // 如果更新失败，至少保留原始数据
        }
    }

    override suspend fun clearUserInfo() {
        MMKVUtils.remove(KEY_USER_INFO)
    }

    override suspend fun getUserId(): Long {
        return getUserInfo()?.id ?: 0L
    }

    override suspend fun getNickName(): String? {
        return getUserInfo()?.nickName
    }

    override suspend fun getAvatarUrl(): String? {
        return getUserInfo()?.avatarUrl
    }

}