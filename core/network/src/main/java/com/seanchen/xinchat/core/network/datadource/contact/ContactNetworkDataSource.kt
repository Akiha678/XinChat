package com.seanchen.xinchat.core.network.datadource.contact

import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.model.response.FriendRequestResponse
import com.seanchen.xinchat.core.model.response.UserSummaryResponse

interface ContactNetworkDataSource {
    suspend fun searchUsers(username: String): List<UserSummaryResponse>

    suspend fun getFriends(): List<UserSummaryResponse>

    suspend fun getIncomingFriendRequests(): List<FriendRequestResponse>

    suspend fun getOutgoingFriendRequests(): List<FriendRequestResponse>

    suspend fun createFriendRequest(request: CreateFriendRequest): FriendRequestResponse

    suspend fun acceptFriendRequest(requestId: Long): FriendRequestResponse

    suspend fun rejectFriendRequest(requestId: Long): FriendRequestResponse
}
