package com.seanchen.xinchat.feature.contact.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.contact.ContactRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.feature.contact.view.ContactRoute
import com.seanchen.xinchat.feature.contact.view.AddFriendRoute
import com.seanchen.xinchat.feature.contact.view.FriendInfoRoute

fun EntryProviderScope<NavKey>.contactGraph() {
    entry<ContactRoutes.Contact> {
        ContactRoute()
    }
    entry<ContactRoutes.AddFriend> {
        AddFriendRoute()
    }
    entry<ContactRoutes.FriendInfo> { route ->
        FriendInfoRoute(userId = route.userId)
    }
}
