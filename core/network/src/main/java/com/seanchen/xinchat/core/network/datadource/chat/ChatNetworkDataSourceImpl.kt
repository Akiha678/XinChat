package com.seanchen.xinchat.core.network.datadource.chat

import com.seanchen.xinchat.core.model.entity.ChatSession
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.model.request.MessagePageRequest
import com.seanchen.xinchat.core.model.request.ReadMessageRequest
import com.seanchen.xinchat.core.model.response.NetworkPageData
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.base.BaseNetworkDataSource
import com.seanchen.xinchat.core.network.service.ChatService
import javax.inject.Inject

class ChatNetworkDataSourceImpl @Inject constructor(
    private val chatService: ChatService
) : BaseNetworkDataSource(), ChatNetworkDataSource {
    override suspend fun createSession(): NetworkResponse<ChatSession> {
        return chatService.createSession()
    }

    override suspend fun readMessage(params: ReadMessageRequest): NetworkResponse<Boolean> {
        return chatService.readMessage(params)
    }

    override suspend fun getMessagePage(
        params: MessagePageRequest
    ): NetworkResponse<NetworkPageData<Msg>> {
        return chatService.getMessagePage(params)
    }

    override suspend fun getUnreadCount(): NetworkResponse<Int> {
        return chatService.getUnreadCount()
    }
}
