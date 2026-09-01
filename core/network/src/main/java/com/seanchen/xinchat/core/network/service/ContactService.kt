package com.seanchen.xinchat.core.network.service

import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.model.response.FriendRequestResponse
import com.seanchen.xinchat.core.model.response.UserSummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ContactService {
    @GET("contact/users/search")
    suspend fun searchUsers(
        @Query("username") username: String
    ): List<UserSummaryResponse>

    @GET("contact/friends")
    suspend fun getFriends(): List<UserSummaryResponse>

    @GET("contact/friend-requests/incoming")
    suspend fun getIncomingFriendRequests(): List<FriendRequestResponse>

    @GET("contact/friend-requests/outgoing")
    suspend fun getOutgoingFriendRequests(): List<FriendRequestResponse>

    @POST("contact/friend-requests")
    suspend fun createFriendRequest(
        @Body request: CreateFriendRequest
    ): FriendRequestResponse

    @POST("contact/friend-requests/{requestId}/accept")
    suspend fun acceptFriendRequest(
        @Path("requestId") requestId: Long
    ): FriendRequestResponse

    @POST("contact/friend-requests/{requestId}/reject")
    suspend fun rejectFriendRequest(
        @Path("requestId") requestId: Long
    ): FriendRequestResponse
}
