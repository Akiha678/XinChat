package com.seanchen.xinchat.core.navigation.auth

import com.seanchen.xinchat.core.navigation.navigate

object AuthNavigator {

    /**
     * 跳转到邮箱密码登录页
     */
    fun toPasswordLogin() {
        navigate(AuthRoutes.PasswordLogin)
    }

    /**
     * 跳转到短信/邮箱验证码登录页
     */
    fun toCodeLogin() {
        navigate(AuthRoutes.CodeLogin)
    }

    /**
     * 跳转到验证码输入页
     */
    fun toCodeVerify(email: String) {
        navigate(AuthRoutes.CodeVerify(email))
    }

    /**
     * 跳转到密码输入页
     */
    fun toPasswordInput(email: String) {
        navigate(AuthRoutes.PasswordInput(email))
    }
}