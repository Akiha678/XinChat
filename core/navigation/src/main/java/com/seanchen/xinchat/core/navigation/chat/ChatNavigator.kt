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
    fun toChatMessage(){
        navigate(ChatRoutes.ChatMessage)
    }
}