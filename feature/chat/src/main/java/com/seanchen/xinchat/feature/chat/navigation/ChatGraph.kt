package com.seanchen.xinchat.feature.chat.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.chat.ChatRoutes
import com.seanchen.xinchat.feature.chat.view.ChatListRoute
import com.seanchen.xinchat.feature.chat.view.ChatMessageRoute

fun EntryProviderScope<NavKey>.chatGraph() {
    /**
     * 聊天列表界面
     */
    entry<ChatRoutes.ChatList> {
        ChatListRoute()
    }

    /**
     * 聊天界面
     */
    entry<ChatRoutes.ChatMessage> {
        ChatMessageRoute()
    }
}