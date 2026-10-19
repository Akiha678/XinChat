package com.seanchen.xinchat.core.navigation.auth

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object AuthRoutes {

    /**
     * 登录入口（默认指向验证码登录）
     */
    val Login: NavKey get() = SmsLogin

    /**
     * 验证码登录界面（输入邮箱 → 获取验证码）
     */
    @Serializable
    data object SmsLogin : NavKey

    /**
     * 验证码输入界面（输入验证码 → 登录）
     *
     * @param email 验证码发送目标邮箱
     */
    @Serializable
    data class SmsCodeVerify(val email: String) : NavKey

    /**
     * 密码登录界面（输入邮箱 → 下一步）
     */
    @Serializable
    data object AccountLogin : NavKey

    /**
     * 密码输入界面（输入密码 → 登录）
     *
     * @param email 登录邮箱账号
     */
    @Serializable
    data class PasswordInput(val email: String) : NavKey
}