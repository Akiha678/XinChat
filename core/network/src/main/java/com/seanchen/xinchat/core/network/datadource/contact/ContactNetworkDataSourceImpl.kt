package com.seanchen.xinchat.core.network.datadource.contact

import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.model.response.FriendRequestResponse
import com.seanchen.xinchat.core.model.response.UserSummaryResponse
import com.seanchen.xinchat.core.network.base.BaseNetworkDataSource
import com.seanchen.xinchat.core.network.service.ContactService
import javax.inject.Inject

class ContactNetworkDataSourceImpl @Inject constructor(
    private val contactService: ContactService
) : BaseNetworkDataSource(), ContactNetworkDataSource {
    override suspend fun searchUsers(username: String): List<UserSummaryResponse> {
        return contactService.searchUsers(username)
    }

    override suspend fun getFriends(): List<UserSummaryResponse> {
        return contactService.getFriends()
    }

    override suspend fun getIncomingFriendRequests(): List<FriendRequestResponse> {
        return contactService.getIncomingFriendRequests()
    }

    override suspend fun getOutgoingFriendRequests(): List<FriendRequestResponse> {
        return contactService.getOutgoingFriendRequests()
    }

    override suspend fun createFriendRequest(request: CreateFriendRequest): FriendRequestResponse {
        return contactService.createFriendRequest(request)
    }

    override suspend fun acceptFriendRequest(requestId: Long): FriendRequestResponse {
        return contactService.acceptFriendRequest(requestId)
    }

    override suspend fun rejectFriendRequest(requestId: Long): FriendRequestResponse {
        return contactService.rejectFriendRequest(requestId)
    }
}
