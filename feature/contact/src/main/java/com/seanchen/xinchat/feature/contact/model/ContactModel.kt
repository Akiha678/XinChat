package com.seanchen.xinchat.feature.contact.model

import com.seanchen.xinchat.core.model.response.UserSummaryResponse

data class ContactModel(
    val searchQuery: String = "",
    val friends: List<ContactUserModel> = emptyList(),
    val searchResults: List<ContactUserModel> = emptyList(),
    val errorMessage: String? = null,
)

/**
 * 好友数据模型
 */
data class ContactUserModel(
    val id: Long,
    val displayName: String,
    val username: String,
    val email: String,
    val avatarColor: Int,
)

fun UserSummaryResponse.toContactUserModel() = ContactUserModel(
    id = id,
    displayName = name.ifBlank { username.ifBlank { email.ifBlank { "未命名" } } },
    username = username, email = email, avatarColor = avatarColor
)
