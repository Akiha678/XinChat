package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.model.entity.ChatSession
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.model.request.MessagePageRequest
import com.seanchen.xinchat.core.model.request.ReadMessageRequest
import com.seanchen.xinchat.core.model.response.NetworkPageData
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.datadource.chat.ChatNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatNetworkDataSource: ChatNetworkDataSource
) {
    /**
     * 创建会话
     */
    fun createSession(): Flow<NetworkResponse<ChatSession>> = flow {
        emit(chatNetworkDataSource.createSession())
    }.flowOn(Dispatchers.IO)

    /**
     * 将消息标记为已读
     */
    fun readMessage(params: ReadMessageRequest): Flow<NetworkResponse<Boolean>> = flow {
        emit(chatNetworkDataSource.readMessage(params))
    }.flowOn(Dispatchers.IO)


    fun getMessagePage(params: MessagePageRequest): Flow<NetworkResponse<NetworkPageData<Msg>>> =
        flow {
            emit(chatNetworkDataSource.getMessagePage(params))
        }.flowOn(Dispatchers.IO)

    fun getUnreadCount(): Flow<NetworkResponse<Int>> = flow {
        emit(chatNetworkDataSource.getUnreadCount())
    }.flowOn(Dispatchers.IO)
}