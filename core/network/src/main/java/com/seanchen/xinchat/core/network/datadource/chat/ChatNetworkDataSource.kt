package com.seanchen.xinchat.core.network.datadource.chat

import com.seanchen.xinchat.core.model.entity.ChatSession
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.model.request.MessagePageRequest
import com.seanchen.xinchat.core.model.request.ReadMessageRequest
import com.seanchen.xinchat.core.model.response.NetworkPageData
import com.seanchen.xinchat.core.model.response.NetworkResponse

interface ChatNetworkDataSource {
    /**
     * 创建会话
     */
    suspend fun createSession(): NetworkResponse<ChatSession>

    /**
     * 获取会话详情
     */
    suspend fun getSessionDetail(): NetworkResponse<ChatSession>

    /**
     * 标记消息为已读
     */
    suspend fun readMessage(params: ReadMessageRequest): NetworkResponse<Boolean>

    /**
     * 分页查询
     */
    suspend fun getMessagePage(params: MessagePageRequest): NetworkResponse<NetworkPageData<Msg>>

    /**
     * 获取未读消息数
     */
    suspend fun getUnreadCount(): NetworkResponse<Int>
}