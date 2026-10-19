package com.seanchen.xinchat.feature.auth.state

import com.seanchen.xinchat.core.util.validation.ValidationUtil

/**
 * 邮箱验证码登录页状态。
 *
 * @param email 邮箱账号
 * @param verificationCode 验证码（4 位数字）
 * @param resendCountdown 重新获取验证码的剩余秒数，0 表示可立即获取
 * @param isSendingCode 是否正在请求验证码
 * @param isLoggingIn 是否正在登录
 */
data class SmsLoginUiState(
    val email: String = "",
    val verificationCode: String = "",
    val resendCountdown: Int = 0,
    val isSendingCode: Boolean = false,
    val isLoggingIn: Boolean = false
) {
    /** 兼容旧代码别名 */
    val phone: String
        get() = email

    /** 邮箱合法且不处于发送中/倒计时中时，允许获取验证码 */
    val canSendCode: Boolean
        get() = ValidationUtil.isValidEmail(email) && !isSendingCode && resendCountdown == 0

    /** 邮箱与验证码都合法且未在登录中时，允许登录 */
    val canLogin: Boolean
        get() = ValidationUtil.isValidEmail(email) &&
                ValidationUtil.isValidSmsCode(verificationCode) &&
                !isLoggingIn
}
