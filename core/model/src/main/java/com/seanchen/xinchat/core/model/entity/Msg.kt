package com.seanchen.xinchat.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Msg (
    /**
     * ID
     */
    val id: Long = 0,

    /**
     * 用户ID
     */
    val userId: Long = 0,

    /**
     * 会话ID
     */
    val sessionId: Long = 0,

    /**
     * 状态 0-未读 1-已读
     */
    val status: Int = 0,

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

    val type: Int = 0,

    /**
     * 更新时间
     */
    val updateTime: String? = null
) {
    /**
     * 消息内容
     */
    @Serializable
    data class MessageContent (
        /**
         * TEXT - 文本
         * IMAGE - 图片
         * VOICE - 语音
         * VIDEO - 视频
         * FILE - 文件
         * LINK - 链接
         * LOCATION - 位置
         * EMOJI - 表情
         */
        val type: String = "",

        val data: String = ""
    )
}