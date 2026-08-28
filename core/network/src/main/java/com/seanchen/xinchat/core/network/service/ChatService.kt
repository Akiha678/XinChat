package com.seanchen.xinchat.core.network.service

import com.seanchen.xinchat.core.model.entity.ChatSession
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.model.request.MessagePageRequest
import com.seanchen.xinchat.core.model.request.ReadMessageRequest
import com.seanchen.xinchat.core.model.response.NetworkPageData
import com.seanchen.xinchat.core.model.response.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatService {
    /**
     * 创建客服会话
     */
    @POST("chat/session")
    suspend fun createSession(): NetworkResponse<ChatSession>

    /**
     * 标记消息为已读
     */
    @POST("chat/message/read")
    suspend fun readMessage(
        @Body params: ReadMessageRequest
    ): NetworkResponse<Boolean>

    /**
     * 分页查询会话消息
     */
    @POST("chat/message/page")
    suspend fun getMessagePage(
        @Body params: MessagePageRequest
    ): NetworkResponse<NetworkPageData<Msg>>

    /**
     * 查询未读消息数
     */
    @GET("chat/message/unread")
    suspend fun getUnreadCount(): NetworkResponse<Int>
}
