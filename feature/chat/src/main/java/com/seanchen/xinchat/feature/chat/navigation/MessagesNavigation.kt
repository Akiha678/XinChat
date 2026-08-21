//package com.seanchen.xinchat.feature.chat.navigation
//
//import androidx.navigation3.runtime.EntryProviderScope
//import androidx.navigation3.runtime.NavKey
//import com.seanchen.xinchat.core.navigation.TopLevelNavKey
//import com.seanchen.xinchat.feature.chat.ui.MessagesRoute
//import com.seanchen.xinchat.feature.chat.ui.ConversationRoute
//import kotlinx.serialization.Serializable
//
//@Serializable
//data object MessagesKey : TopLevelNavKey {
//    override val route: String = "messages"
//}
//
//@Serializable
//data class ConversationKey(val conversationId: Long, val name: String) : NavKey
//
//fun EntryProviderScope<NavKey>.messagesEntry(
//    onConversationClick: (ConversationKey) -> Unit,
//    onConversationBack: () -> Unit,
//) {
//    entry<MessagesKey> { MessagesRoute(onConversationClick = onConversationClick) }
//    entry<ConversationKey> { key ->
//        ConversationRoute(
//            conversationId = key.conversationId,
//            conversationName = key.name,
//            onBack = onConversationBack,
//        )
//    }
//}
