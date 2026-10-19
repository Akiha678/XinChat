package com.seanchen.xinchat.core.navigation.auth

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object AuthRoutes {

    /**
     * 登录主页路由（邮箱验证码登录）
     */
    @Serializable
    data object Login : NavKey

    /**
     * 邮箱密码登录（第一步：输入邮箱）
     */
    @Serializable
    data object AccountLogin : NavKey

    /**
     * 验证码登录别名
     */
    @Serializable
    data object SmsLogin : NavKey

    /**
     * 验证码输入界面（第二步：输入验证码）
     */
    @Serializable
    data class SmsCodeVerify(val phone: String) : NavKey

    /**
     * 密码输入界面（第二步：输入密码）
     */
    @Serializable
    data class PasswordInput(val account: String) : NavKey
}