package com.seanchen.xinchat.core.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface XinChatApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): LoginResponseDto

    @GET("api/v1/users/search")
    suspend fun searchUsers(
        @Header("Authorization") authorization: String,
        @Query("username") username: String,
    ): List<UserDto>

    @GET("api/v1/friends")
    suspend fun friends(@Header("Authorization") authorization: String): List<UserDto>

    @GET("api/v1/friend-requests/incoming")
    suspend fun incomingRequests(
        @Header("Authorization") authorization: String,
    ): List<FriendRequestDto>

    @GET("api/v1/friend-requests/outgoing")
    suspend fun outgoingRequests(
        @Header("Authorization") authorization: String,
    ): List<FriendRequestDto>

    @POST("api/v1/friend-requests")
    suspend fun sendFriendRequest(
        @Header("Authorization") authorization: String,
        @Body request: CreateFriendRequestDto,
    ): FriendRequestDto

    @POST("api/v1/friend-requests/{requestId}/accept")
    suspend fun acceptFriendRequest(
        @Header("Authorization") authorization: String,
        @Path("requestId") requestId: Long,
    ): FriendRequestDto

    @POST("api/v1/friend-requests/{requestId}/reject")
    suspend fun rejectFriendRequest(
        @Header("Authorization") authorization: String,
        @Path("requestId") requestId: Long,
    ): FriendRequestDto

    @GET("api/v1/conversations")
    suspend fun conversations(
        @Header("Authorization") authorization: String,
    ): List<ConversationDto>

    @POST("api/v1/conversations/direct")
    suspend fun createDirectConversation(
        @Header("Authorization") authorization: String,
        @Body request: CreateDirectConversationDto,
    ): ConversationDto

    @GET("api/v1/conversations/{conversationId}/messages")
    suspend fun messages(
        @Header("Authorization") authorization: String,
        @Path("conversationId") conversationId: Long,
        @Query("beforeMessageId") beforeMessageId: Long? = null,
        @Query("limit") limit: Int = 100,
    ): List<ChatMessageDto>

    @POST("api/v1/conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Path("conversationId") conversationId: Long,
        @Body request: SendMessageDto,
    ): ChatMessageDto

    @POST("api/v1/conversations/{conversationId}/read")
    suspend fun markRead(
        @Header("Authorization") authorization: String,
        @Path("conversationId") conversationId: Long,
    )
}
