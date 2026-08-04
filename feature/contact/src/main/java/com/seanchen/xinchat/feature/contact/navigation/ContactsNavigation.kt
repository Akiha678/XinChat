package com.seanchen.xinchat.feature.contact.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.TopLevelNavKey
import com.seanchen.xinchat.feature.contact.ui.ContactsRoute
import kotlinx.serialization.Serializable

@Serializable
data object ContactsKey : TopLevelNavKey {
    override val route: String = "contacts"
}

fun EntryProviderScope<NavKey>.contactsEntry(
    onConversationOpened: (conversationId: Long, name: String) -> Unit,
) {
    entry<ContactsKey> { ContactsRoute(onConversationOpened = onConversationOpened) }
}
