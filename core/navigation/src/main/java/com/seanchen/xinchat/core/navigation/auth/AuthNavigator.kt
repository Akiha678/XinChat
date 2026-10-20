package com.seanchen.xinchat.core.navigation.auth

import com.seanchen.xinchat.core.navigation.NavigationService
import com.seanchen.xinchat.core.navigation.navigate

object AuthNavigator {

    /**
     * 跳转到邮箱密码登录页（若从验证码登录页切换，则关闭原页面，防止导航返回栈无限堆叠）
     */
    fun toPasswordLogin() {
        NavigationService.navigateAndCloseCurrent(
            route = AuthRoutes.PasswordLogin,
            currentRoute = AuthRoutes.CodeLogin
        )
    }

    /**
     * 跳转到邮箱验证码登录页（若从密码登录页切换，则关闭原页面，防止导航返回栈无限堆叠）
     */
    fun toCodeLogin() {
        NavigationService.navigateAndCloseCurrent(
            route = AuthRoutes.CodeLogin,
            currentRoute = AuthRoutes.PasswordLogin
        )
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
