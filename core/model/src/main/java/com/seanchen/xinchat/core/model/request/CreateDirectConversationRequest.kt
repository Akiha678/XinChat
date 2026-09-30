package com.seanchen.xinchat.core.model.request

import kotlinx.serialization.Serializable

/**
 * 创建或获取单聊会话请求体
 */
@Serializable
data class CreateDirectConversationRequest(
    val friendId: Long
)
