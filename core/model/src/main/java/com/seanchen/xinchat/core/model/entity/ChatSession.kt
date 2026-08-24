package com.seanchen.xinchat.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class ChatSession (
    /**
     * ID
     */
    val id: Long = 0,

    /**
     * 用户ID
     */
    val userId: Long = 0,

    /**
     * 最后一条消息
     */
    val lastMsg: Msg? = null,

    /**
     * 未读消息
     */
    val unreadCount: Long = 0,

    /**
     * 用户昵称
     */
    val nickName: String = "",

    /**
     * 用户头像
     */
    val avatarUrl: String = "",

    /**
     * 创建时间
     */
    val createTime: String? = null,

    /**
     * 更新时间
     */
    val updateTime: String? = null
)