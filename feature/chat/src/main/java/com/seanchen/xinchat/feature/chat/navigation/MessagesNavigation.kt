package com.seanchen.xinchat.feature.chat.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.TopLevelNavKey
import com.seanchen.xinchat.feature.chat.ui.MessagesRoute
import kotlinx.serialization.Serializable

@Serializable
data object MessagesKey : TopLevelNavKey {
    override val route: String = "messages"
}

fun EntryProviderScope<NavKey>.messagesEntry() {
    entry<MessagesKey> { MessagesRoute() }
}
