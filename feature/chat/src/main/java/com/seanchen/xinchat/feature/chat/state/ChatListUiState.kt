package com.seanchen.xinchat.feature.chat.state

data class ChatListUiState(
    val sessions: List<ChatSessionItemUiState> = emptyList()
)

/**
 * 聊天列表状态
 */
data class ChatSessionItemUiState(
    val id: Long,
    val peerId: Long,
    val name: String,
    val preview: String,
    val lastMessageAt: String?,
    val unreadCount: Int,
    val colorSeed: Int,
    val avatarUrl: String = "",     // 头像URL
    val isPinned: Boolean = false,  // 已固定
    val isMuted: Boolean = false    // 已静音
)
