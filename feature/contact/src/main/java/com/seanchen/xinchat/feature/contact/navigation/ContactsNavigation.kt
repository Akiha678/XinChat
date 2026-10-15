package com.seanchen.xinchat.feature.contact.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.contact.ContactRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.feature.contact.view.ContactRoute
import com.seanchen.xinchat.feature.contact.view.AddFriendRoute
import com.seanchen.xinchat.feature.contact.view.FriendInfoRoute

fun EntryProviderScope<NavKey>.contactGraph() {
    /**
     * 联系人界面路由
     */
    entry<ContactRoutes.Contact> {
        ContactRoute()
    }

    /**
     * 添加好友路由
     */
    entry<ContactRoutes.AddFriend> {
        AddFriendRoute()
    }

    /**
     * 朋友信息路由
     */
    entry<ContactRoutes.FriendInfo> { route ->
        FriendInfoRoute(userId = route.userId)
    }
}
