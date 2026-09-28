package com.seanchen.xinchat.feature.chat.state

/**
 * 聊天信息界面 UI 状态
 */
data class ChatInfoUiState(
    val sessionId: Long = 0L,
    val name: String = "",
    val avatarUrl: String = "",
    val peerId: Long = 0L,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isLoading: Boolean = false
)
