package com.seanchen.xinchat.feature.auth.state

import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.util.validation.ValidationUtil

/**
 * 登录方式枚举
 */
enum class LoginMode {
    /** 邮箱验证码登录 */
    CODE,
    /** 邮箱密码登录 */
    PASSWORD
}

/**
 * 统一登录界面 UI 状态（涵盖验证码登录与密码登录全流程）。
 *
 * @param email 登录邮箱（用户名）
 * @param password 登录密码
 * @param verificationCode 邮箱验证码（4 位纯数字）
 * @param resendCountdown 重新获取验证码倒计时剩余秒数（0 表示可立即获取）
 * @param isSendingCode 是否正在请求验证码
 * @param isLoggingIn 是否正在执行登录
 * @param isAgreed 是否同意用户协议与隐私政策
 * @param loginMode 当前选择的登录方式
 * @param showCaptchaDialog 是否展示图形验证码防人机弹窗
 * @param captcha 当前获取到的图形验证码数据
 * @param isLoadingCaptcha 是否正在加载图形验证码
 * @param isVerifyingCaptcha 是否正在校验图形验证码
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val verificationCode: String = "",
    val resendCountdown: Int = 0,
    val isSendingCode: Boolean = false,
    val isLoggingIn: Boolean = false,
    val isAgreed: Boolean = false,
    val loginMode: LoginMode = LoginMode.CODE,
    val showCaptchaDialog: Boolean = false,
    val captcha: Captcha = Captcha(),
    val isLoadingCaptcha: Boolean = false,
    val isVerifyingCaptcha: Boolean = false
) {
    /** 邮箱格式是否合法 */
    val isEmailValid: Boolean
        get() = ValidationUtil.isValidEmail(email.trim())

    /** 密码格式是否合法（不少于 6 位） */
    val isPasswordValid: Boolean
        get() = ValidationUtil.isValidPassword(password)

    /** 验证码格式是否合法（4 位纯数字） */
    val isVerificationCodeValid: Boolean
        get() = ValidationUtil.isValidCode(verificationCode)

    /** 验证码登录步骤一：是否允许点击“获取验证码” */
    val canSendCode: Boolean
        get() = isEmailValid && !isSendingCode && resendCountdown == 0

    /** 密码登录步骤一：是否允许点击“下一步” */
    val canGoNext: Boolean
        get() = isEmailValid

    /** 验证码登录步骤二：是否允许点击“登录” */
    val canCodeLogin: Boolean
        get() = isEmailValid && isVerificationCodeValid && !isLoggingIn

    /** 密码登录步骤二：是否允许点击“登录” */
    val canPasswordLogin: Boolean
        get() = isEmailValid && isPasswordValid && !isLoggingIn

    /** 通用登录按钮状态（根据填入的凭证自动判定） */
    val canLogin: Boolean
        get() = if (verificationCode.isNotEmpty()) canCodeLogin else canPasswordLogin
}
