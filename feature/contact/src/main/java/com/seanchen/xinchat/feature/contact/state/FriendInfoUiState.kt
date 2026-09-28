package com.seanchen.xinchat.feature.contact.state

data class FriendInfoUiState(
    val id: Long = 0L,
    val displayName: String = "好友",
    val username: String = "",
    val remark: String = "",
    val region: String = "中国大陆",
    val signature: String = "",
    val avatarUrl: String? = null,
    val avatarColor: Int = 0,
    val isOnline: Boolean = false,
    val source: String = "通过搜索添加"
)

fun ContactUserUiState.toFriendInfoUiState(
    remark: String = "",
    region: String = "中国大陆",
    signature: String = "",
    source: String = "通过搜索添加"
): FriendInfoUiState = FriendInfoUiState(
    id = id,
    displayName = displayName,
    username = username,
    remark = remark,
    region = region,
    signature = signature,
    avatarUrl = avatarUrl,
    avatarColor = avatarColor,
    isOnline = isOnline,
    source = source
)