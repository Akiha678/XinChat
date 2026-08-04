package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.data.model.ChatMessage
import com.seanchen.xinchat.core.data.model.Conversation
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ChatRepository {
    val conversations: StateFlow<List<Conversation>>
    val incomingMessages: SharedFlow<ChatMessage>

    fun messages(conversationId: Long): StateFlow<List<ChatMessage>>
    suspend fun refreshConversations()
    suspend fun refreshMessages(conversationId: Long)
    suspend fun openDirectConversation(friendId: Long): Conversation
    suspend fun sendMessage(conversationId: Long, content: String)
    suspend fun markRead(conversationId: Long)
}
