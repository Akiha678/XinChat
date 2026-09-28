package com.seanchen.xinchat.feature.chat.state

/**
 * 聊天信息界面 UI 状态
 */
data class ChatInfoUiState(
    val sessionId: Long = 0L,
    val name: String = "",
    val avatarUrl: String = "",
    val peerId: Long = 0L,
    val isPinned: Boolean = false,  // 已固定
    val isMuted: Boolean = false,   // 已静音
    val isLoading: Boolean = false  // 加载中
)
