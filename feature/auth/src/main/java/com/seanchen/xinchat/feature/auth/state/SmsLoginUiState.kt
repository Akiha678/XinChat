package com.seanchen.xinchat.feature.auth.state

import com.seanchen.xinchat.core.util.validation.ValidationUtil

/**
 * 手机号验证码登录页状态。
 *
 * @param phone 手机号（不含 +86 前缀，最多 11 位）
 * @param verificationCode 短信验证码（4 位数字）
 * @param resendCountdown 重新获取验证码的剩余秒数，0 表示可立即获取
 * @param isSendingCode 是否正在请求验证码
 * @param isLoggingIn 是否正在登录
 */
data class SmsLoginUiState(
    val phone: String = "",
    val verificationCode: String = "",
    val resendCountdown: Int = 0,
    val isSendingCode: Boolean = false,
    val isLoggingIn: Boolean = false
) {
    /** 手机号合法且不处于发送中/倒计时中时，允许获取验证码 */
    val canSendCode: Boolean
        get() = ValidationUtil.isValidPhone(phone) && !isSendingCode && resendCountdown == 0

    /** 手机号与验证码都合法且未在登录中时，允许登录 */
    val canLogin: Boolean
        get() = ValidationUtil.isValidPhone(phone) &&
                ValidationUtil.isValidSmsCode(verificationCode) &&
                !isLoggingIn
}
