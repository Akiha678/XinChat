package com.seanchen.xinchat.feature.chat.state

data class ChatListUiState(
    val sessions: List<ChatSessionItemUiState> = emptyList()
)

data class ChatSessionItemUiState(
    val id: Long,
    val peerId: Long,
    val name: String,
    val preview: String,
    val lastMessageAt: String?,
    val unreadCount: Int,
    val colorSeed: Int,
)
