package com.seanchen.xinchat.core.model.request

import kotlinx.serialization.Serializable

/**
 * 修改用户昵称请求模型
 *
 * @param nickName 用户新昵称，不能为空且 <= 80 字符
 */
@Serializable
data class UpdateNicknameRequest(
    val nickName: String
)
