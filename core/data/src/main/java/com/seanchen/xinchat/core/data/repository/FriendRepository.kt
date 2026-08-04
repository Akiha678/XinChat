package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.data.model.FriendRequest
import com.seanchen.xinchat.core.data.model.User
import kotlinx.coroutines.flow.StateFlow

interface FriendRepository {
    val friends: StateFlow<List<User>>
    val incomingRequests: StateFlow<List<FriendRequest>>
    val outgoingRequests: StateFlow<List<FriendRequest>>

    suspend fun refresh()
    suspend fun search(username: String): List<User>
    suspend fun sendRequest(addresseeId: Long, message: String?)
    suspend fun acceptRequest(requestId: Long)
    suspend fun rejectRequest(requestId: Long)
}
