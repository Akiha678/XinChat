package com.seanchen.xinchat.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 会话列表接口返回的单聊摘要。
 *
 * 该模型只描述会话列表需要的字段，消息详情仍由 [ChatSession] 和 [Msg] 承担。
 */
@Serializable
data class Conversation(
    val id: Long = 0,
    val peerId: Long = 0,
    val name: String = "",
    val preview: String = "",
    val lastMessageAt: String? = null,
    val unreadCount: Int = 0,
    val colorSeed: Int = 0
)
