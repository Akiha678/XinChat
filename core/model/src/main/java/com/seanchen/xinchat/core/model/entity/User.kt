package com.seanchen.xinchat.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    /**
     * ID
     */
    val id: Long = 0,

    /**
     * 登录唯一ID
     */
    val unionid: String = "",

    /**
     * 头像
     */
    val avatarUrl: String? = null,

    /**
     * 昵称
     */
    val nickName: String? = null
)