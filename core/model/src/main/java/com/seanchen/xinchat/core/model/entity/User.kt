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
    val nickName: String? = null,

    /**
     * 手机号
     */
    val phone: String? = null,

    /**
     * 性别
     */
    val gender: Int = 0,

    /**
     * 状态
     */
    val status: Int = 1,

    /**
     * 登录方式
     */
    val loginType: String = "0",

    /**
     * 密码
     */
    val password: String? = null,

    /**
     * 创建时间
     */
    val createTime: String? = null,

    /**
     * 更新时间
     */
    val updateTime: String? = null
)