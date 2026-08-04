package com.seanchen.xinchat.core.data.network

import com.seanchen.xinchat.core.data.model.ChatMessage
import com.seanchen.xinchat.core.data.model.Conversation
import com.seanchen.xinchat.core.data.model.FriendRequest
import com.seanchen.xinchat.core.data.model.FriendRequestStatus
import com.seanchen.xinchat.core.data.model.User
import com.seanchen.xinchat.core.data.model.UserSession
import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequestDto(val username: String, val password: String)

@Serializable
internal data class RegisterRequestDto(
    val username: String,
    val displayName: String,
    val email: String,
    val password: String,
)

@Serializable
internal data class LoginResponseDto(
    val id: Long,
    val username: String,
    val displayName: String,
    val email: String,
    val avatarColor: Int,
    val accessToken: String,
    val expiresAt: String,
)

@Serializable
internal data class UserDto(
    val id: Long,
    val name: String,
    val username: String,
    val email: String,
    val avatarColor: Int,
)

@Serializable
internal data class FriendRequestDto(
    val id: Long,
    val requester: UserDto,
    val addressee: UserDto,
    val status: String,
    val message: String? = null,
    val createdAt: String,
)

@Serializable
internal data class CreateFriendRequestDto(
    val addresseeId: Long,
    val message: String? = null,
)

@Serializable
internal data class ConversationDto(
    val id: Long,
    val peerId: Long,
    val name: String,
    val preview: String,
    val lastMessageAt: String,
    val unreadCount: Int,
    val colorSeed: Int,
)

@Serializable
internal data class CreateDirectConversationDto(val friendId: Long)

@Serializable
internal data class ChatMessageDto(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val content: String,
    val type: String,
    val createdAt: String,
)

@Serializable
internal data class SendMessageDto(val content: String)

@Serializable
internal data class ApiErrorDto(val message: String? = null)

internal fun LoginResponseDto.toModel() = UserSession(
    user = User(
        id = id,
        username = username,
        displayName = displayName,
        email = email,
        avatarColor = avatarColor,
    ),
    accessToken = accessToken,
    expiresAt = expiresAt,
)

internal fun UserDto.toModel() = User(
    id = id,
    username = username,
    displayName = name,
    email = email,
    avatarColor = avatarColor,
)

internal fun FriendRequestDto.toModel() = FriendRequest(
    id = id,
    requester = requester.toModel(),
    addressee = addressee.toModel(),
    status = runCatching { FriendRequestStatus.valueOf(status) }
        .getOrDefault(FriendRequestStatus.PENDING),
    message = message,
    createdAt = createdAt,
)

internal fun ConversationDto.toModel() = Conversation(
    id = id,
    peerId = peerId,
    name = name,
    preview = preview,
    lastMessageAt = lastMessageAt,
    unreadCount = unreadCount,
    colorSeed = colorSeed,
)

internal fun ChatMessageDto.toModel() = ChatMessage(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    content = content,
    createdAt = createdAt,
)
