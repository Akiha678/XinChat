package com.seanchen.xinchat.feature.chat.state

data class ChatListUiState(
    val sessions: List<ChatSessionItemUiState> = emptyList()
)

data class ChatSessionItemUiState(
    val id: Long,
    val unreadCount: Int,
)
