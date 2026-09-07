package com.seanchen.xinchat.core.navigation.chat

import com.seanchen.xinchat.core.navigation.navigate

object ChatNavigator {
    /**
     * 跳转到聊天列表界面
     */
    fun toChatList(){
        navigate(ChatRoutes.ChatList)
    }

    /**
     * 跳转到聊天界面
     */
    fun toChatMessage(sessionId: Long){
        navigate(ChatRoutes.ChatMessage(sessionId))
    }

    /**
     * 从没有指定会话的入口打开聊天页，由聊天页按默认会话兼容处理。
     */
    fun toChatMessage(){
        toChatMessage(sessionId = 0L)
    }
}
