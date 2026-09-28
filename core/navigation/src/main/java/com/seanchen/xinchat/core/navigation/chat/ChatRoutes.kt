package com.seanchen.xinchat.core.navigation.chat

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object ChatRoutes {
    /**
     * 聊天列表路由
     */
    @Serializable
    data object ChatList : NavKey

    /**
     * 聊天界面
     */
    @Serializable
    data class ChatMessage(val sessionId: Long) : NavKey

    /**
     * 聊天信息界面
     */
    @Serializable
    data class ChatInfo(val sessionId: Long) : NavKey
}
