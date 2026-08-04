package com.seanchen.xinchat.core.data.model

data class UserSession(
    val user: User,
    val accessToken: String,
    val expiresAt: String,
)

data class User(
    val id: Long,
    val username: String,
    val displayName: String,
    val email: String,
    val avatarColor: Int,
)

data class Conversation(
    val id: Long,
    val peerId: Long,
    val name: String,
    val preview: String,
    val lastMessageAt: String,
    val unreadCount: Int,
    val colorSeed: Int,
)

data class ChatMessage(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val content: String,
    val createdAt: String,
)

data class FriendRequest(
    val id: Long,
    val requester: User,
    val addressee: User,
    val status: FriendRequestStatus,
    val message: String?,
    val createdAt: String,
)

enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
}

sealed interface RealtimeEvent {
    data class MessageCreated(val message: ChatMessage) : RealtimeEvent
    data class FriendRequestChanged(val requestId: Long) : RealtimeEvent
    data class FriendshipAccepted(val conversationId: Long) : RealtimeEvent
    data class ConversationRead(val conversationId: Long) : RealtimeEvent
}
