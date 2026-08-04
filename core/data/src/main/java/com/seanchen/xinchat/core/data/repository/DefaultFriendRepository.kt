package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.data.di.ApplicationScope
import com.seanchen.xinchat.core.data.model.FriendRequest
import com.seanchen.xinchat.core.data.model.RealtimeEvent
import com.seanchen.xinchat.core.data.model.User
import com.seanchen.xinchat.core.data.network.CreateFriendRequestDto
import com.seanchen.xinchat.core.data.network.XinChatApi
import com.seanchen.xinchat.core.data.network.apiCall
import com.seanchen.xinchat.core.data.network.authorizationHeader
import com.seanchen.xinchat.core.data.network.toModel
import com.seanchen.xinchat.core.data.realtime.ChatRealtimeClient
import com.seanchen.xinchat.core.data.session.SessionStore
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal class DefaultFriendRepository @Inject constructor(
    private val api: XinChatApi,
    private val sessionStore: SessionStore,
    private val realtimeClient: ChatRealtimeClient,
    private val json: Json,
    @ApplicationScope scope: CoroutineScope,
) : FriendRepository {
    private val mutableFriends = MutableStateFlow<List<User>>(emptyList())
    private val mutableIncoming = MutableStateFlow<List<FriendRequest>>(emptyList())
    private val mutableOutgoing = MutableStateFlow<List<FriendRequest>>(emptyList())

    override val friends = mutableFriends.asStateFlow()
    override val incomingRequests = mutableIncoming.asStateFlow()
    override val outgoingRequests = mutableOutgoing.asStateFlow()

    init {
        scope.launch {
            realtimeClient.events.collect { event ->
                if (event is RealtimeEvent.FriendshipAccepted ||
                    event is RealtimeEvent.FriendRequestChanged
                ) {
                    runCatching { refresh() }
                }
            }
        }
    }

    override suspend fun refresh() {
        val authorization = sessionStore.authorizationHeader()
        val (friendsResult, incomingResult, outgoingResult) = apiCall(json, sessionStore) {
            Triple(
                api.friends(authorization),
                api.incomingRequests(authorization),
                api.outgoingRequests(authorization),
            )
        }
        mutableFriends.value = friendsResult.map { it.toModel() }
        mutableIncoming.value = incomingResult.map { it.toModel() }
        mutableOutgoing.value = outgoingResult.map { it.toModel() }
    }

    override suspend fun search(username: String): List<User> {
        if (username.isBlank()) return emptyList()
        val authorization = sessionStore.authorizationHeader()
        return apiCall(json, sessionStore) {
            api.searchUsers(authorization, username.trim())
        }.map { it.toModel() }
    }

    override suspend fun sendRequest(addresseeId: Long, message: String?) {
        val authorization = sessionStore.authorizationHeader()
        apiCall(json, sessionStore) {
            api.sendFriendRequest(
                authorization,
                CreateFriendRequestDto(addresseeId, message?.trim()?.takeIf(String::isNotEmpty)),
            )
        }
        refresh()
    }

    override suspend fun acceptRequest(requestId: Long) {
        val authorization = sessionStore.authorizationHeader()
        apiCall(json, sessionStore) { api.acceptFriendRequest(authorization, requestId) }
        refresh()
    }

    override suspend fun rejectRequest(requestId: Long) {
        val authorization = sessionStore.authorizationHeader()
        apiCall(json, sessionStore) { api.rejectFriendRequest(authorization, requestId) }
        refresh()
    }
}
