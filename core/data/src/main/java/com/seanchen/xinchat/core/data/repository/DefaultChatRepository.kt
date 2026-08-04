package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.data.di.ApplicationScope
import com.seanchen.xinchat.core.data.model.ChatMessage
import com.seanchen.xinchat.core.data.model.Conversation
import com.seanchen.xinchat.core.data.model.RealtimeEvent
import com.seanchen.xinchat.core.data.network.CreateDirectConversationDto
import com.seanchen.xinchat.core.data.network.SendMessageDto
import com.seanchen.xinchat.core.data.network.XinChatApi
import com.seanchen.xinchat.core.data.network.apiCall
import com.seanchen.xinchat.core.data.network.authorizationHeader
import com.seanchen.xinchat.core.data.network.toModel
import com.seanchen.xinchat.core.data.realtime.ChatRealtimeClient
import com.seanchen.xinchat.core.data.session.SessionStore
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal class DefaultChatRepository @Inject constructor(
    private val api: XinChatApi,
    private val sessionStore: SessionStore,
    private val realtimeClient: ChatRealtimeClient,
    private val json: Json,
    @param:ApplicationScope private val scope: CoroutineScope,
) : ChatRepository {
    private val mutableConversations = MutableStateFlow<List<Conversation>>(emptyList())
    private val mutableIncomingMessages = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 32)
    private val messagesByConversation = ConcurrentHashMap<Long, MutableStateFlow<List<ChatMessage>>>()

    override val conversations = mutableConversations.asStateFlow()
    override val incomingMessages = mutableIncomingMessages.asSharedFlow()

    init {
        scope.launch {
            sessionStore.session.collect { session ->
                if (session == null) {
                    realtimeClient.disconnect()
                    mutableConversations.value = emptyList()
                    messagesByConversation.values.forEach { it.value = emptyList() }
                } else {
                    realtimeClient.connect(session.accessToken)
                }
            }
        }
        scope.launch {
            realtimeClient.events.collect(::handleRealtimeEvent)
        }
    }

    override fun messages(conversationId: Long): StateFlow<List<ChatMessage>> =
        messagesFlow(conversationId).asStateFlow()

    override suspend fun refreshConversations() {
        val authorization = sessionStore.authorizationHeader()
        mutableConversations.value = apiCall(json, sessionStore) {
            api.conversations(authorization)
        }.map { it.toModel() }
    }

    override suspend fun refreshMessages(conversationId: Long) {
        val authorization = sessionStore.authorizationHeader()
        messagesFlow(conversationId).value = apiCall(json, sessionStore) {
            api.messages(authorization, conversationId)
        }.map { it.toModel() }
    }

    override suspend fun openDirectConversation(friendId: Long): Conversation {
        val authorization = sessionStore.authorizationHeader()
        val conversation = apiCall(json, sessionStore) {
            api.createDirectConversation(authorization, CreateDirectConversationDto(friendId))
        }.toModel()
        mutableConversations.update { current ->
            (current.filterNot { it.id == conversation.id } + conversation)
                .sortedByDescending(Conversation::lastMessageAt)
        }
        return conversation
    }

    override suspend fun sendMessage(conversationId: Long, content: String) {
        val normalized = content.trim()
        if (normalized.isEmpty()) return
        val authorization = sessionStore.authorizationHeader()
        val message = apiCall(json, sessionStore) {
            api.sendMessage(authorization, conversationId, SendMessageDto(normalized))
        }.toModel()
        appendMessage(message)
        refreshConversations()
    }

    override suspend fun markRead(conversationId: Long) {
        val authorization = sessionStore.authorizationHeader()
        apiCall(json, sessionStore) { api.markRead(authorization, conversationId) }
        mutableConversations.update { conversations ->
            conversations.map { conversation ->
                if (conversation.id == conversationId) conversation.copy(unreadCount = 0)
                else conversation
            }
        }
    }

    private suspend fun handleRealtimeEvent(event: RealtimeEvent) {
        when (event) {
            is RealtimeEvent.MessageCreated -> {
                appendMessage(event.message)
                val currentUserId = sessionStore.current()?.user?.id
                if (event.message.senderId != currentUserId) {
                    mutableIncomingMessages.emit(event.message)
                }
                runCatching { refreshConversations() }
            }
            is RealtimeEvent.FriendshipAccepted,
            is RealtimeEvent.ConversationRead,
            -> runCatching { refreshConversations() }
            is RealtimeEvent.FriendRequestChanged -> Unit
        }
    }

    private fun appendMessage(message: ChatMessage) {
        messagesFlow(message.conversationId).update { current ->
            (current.filterNot { it.id == message.id } + message).sortedBy(ChatMessage::id)
        }
    }

    private fun messagesFlow(conversationId: Long): MutableStateFlow<List<ChatMessage>> =
        messagesByConversation.getOrPut(conversationId) { MutableStateFlow(emptyList()) }
}
