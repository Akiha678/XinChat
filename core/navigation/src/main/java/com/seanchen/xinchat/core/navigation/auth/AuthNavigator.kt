package com.seanchen.xinchat.core.navigation.auth

import com.seanchen.xinchat.core.navigation.navigate

object AuthNavigator {
    /**
     * 跳转到登录主页（邮箱验证码登录）
     */
    fun toLogin() {
        navigate(AuthRoutes.Login)
    }

    /**
     * 跳转到邮箱密码登录页
     */
    fun toAccountLogin() {
        navigate(AuthRoutes.AccountLogin)
    }

    /**
     * 跳转到短信/邮箱验证码登录页
     */
    fun toSmsLogin() {
        navigate(AuthRoutes.SmsLogin)
    }

    /**
     * 跳转到验证码输入页
     */
    fun toSmsCodeVerify(phone: String) {
        navigate(AuthRoutes.SmsCodeVerify(phone))
    }

    /**
     * 跳转到密码输入页
     */
    fun toPasswordInput(account: String) {
        navigate(AuthRoutes.PasswordInput(account))
    }
}