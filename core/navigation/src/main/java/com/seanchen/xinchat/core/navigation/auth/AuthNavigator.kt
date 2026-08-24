package com.seanchen.xinchat.core.navigation.auth

import com.seanchen.xinchat.core.navigation.navigate

object AuthNavigator {
    /**
     * 跳转到账号密码登录页
     */
    fun toAccountLogin() {
        navigate(AuthRoutes.AccountLogin)
    }

    /**
     * 跳转到短信验证码登录页
     */
    fun toSmsLogin() {
        navigate(AuthRoutes.SmsLogin)
    }

    /**
     * 跳转到注册页
     */
    fun toRegister() {
        navigate(AuthRoutes.Register)
    }

    /**
     * 跳转到重置密码界面
     */
    fun toResetPassword() {
        navigate(AuthRoutes.ResetPassword)
    }
}