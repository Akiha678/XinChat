package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.model.response.FriendRequestResponse
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.model.response.UserSummaryResponse
import com.seanchen.xinchat.core.network.datadource.contact.ContactNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactNetworkDataSource: ContactNetworkDataSource
) {
    fun searchUsers(username: String): Flow<NetworkResponse<List<UserSummaryResponse>>> = flow {
        emit(NetworkResponse(data = contactNetworkDataSource.searchUsers(username)))
    }.flowOn(Dispatchers.IO)

    fun getFriends(): Flow<NetworkResponse<List<UserSummaryResponse>>> = flow {
        emit(NetworkResponse(data = contactNetworkDataSource.getFriends()))
    }.flowOn(Dispatchers.IO)

    fun getIncomingFriendRequests(): Flow<NetworkResponse<List<FriendRequestResponse>>> = flow {
        emit(NetworkResponse(data = contactNetworkDataSource.getIncomingFriendRequests()))
    }.flowOn(Dispatchers.IO)

    fun getOutgoingFriendRequests(): Flow<NetworkResponse<List<FriendRequestResponse>>> = flow {
        emit(NetworkResponse(data = contactNetworkDataSource.getOutgoingFriendRequests()))
    }.flowOn(Dispatchers.IO)

    fun createFriendRequest(request: CreateFriendRequest): Flow<NetworkResponse<FriendRequestResponse>> = flow {
        emit(NetworkResponse(data = contactNetworkDataSource.createFriendRequest(request)))
    }.flowOn(Dispatchers.IO)

    fun acceptFriendRequest(requestId: Long): Flow<NetworkResponse<FriendRequestResponse>> = flow {
        emit(NetworkResponse(data = contactNetworkDataSource.acceptFriendRequest(requestId)))
    }.flowOn(Dispatchers.IO)

    fun rejectFriendRequest(requestId: Long): Flow<NetworkResponse<FriendRequestResponse>> = flow {
        emit(NetworkResponse(data = contactNetworkDataSource.rejectFriendRequest(requestId)))
    }.flowOn(Dispatchers.IO)
}
