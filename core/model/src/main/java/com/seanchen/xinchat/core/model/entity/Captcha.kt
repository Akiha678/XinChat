package com.seanchen.xinchat.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Captcha (
    /**
     * base64编码的图片验证码
     */
    val data: String = "",

    /**
     * 验证码ID
     */
    val captchaId: String = ""
)