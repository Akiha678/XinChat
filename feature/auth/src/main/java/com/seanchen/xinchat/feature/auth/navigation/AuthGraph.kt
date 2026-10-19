package com.seanchen.xinchat.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.feature.auth.view.AccountLoginRoute
import com.seanchen.xinchat.feature.auth.view.LoginRoute
import com.seanchen.xinchat.feature.auth.view.PasswordInputRoute
import com.seanchen.xinchat.feature.auth.view.SmsCodeVerifyRoute
import com.seanchen.xinchat.feature.auth.view.SmsLoginRoute

fun EntryProviderScope<NavKey>.authGraph() {
    /**
     * 登录主界面（邮箱验证码登录）
     */
    entry<AuthRoutes.Login> {
        LoginRoute()
    }

    /**
     * 密码登录界面（第一步：输入邮箱）
     */
    entry<AuthRoutes.AccountLogin> {
        AccountLoginRoute()
    }

    /**
     * 验证码登录界面（第一步：输入邮箱）
     */
    entry<AuthRoutes.SmsLogin> {
        SmsLoginRoute()
    }

    /**
     * 验证码输入界面（第二步：输入验证码）
     */
    entry<AuthRoutes.SmsCodeVerify> { route ->
        SmsCodeVerifyRoute(phone = route.phone)
    }

    /**
     * 密码输入界面（第二步：输入密码）
     */
    entry<AuthRoutes.PasswordInput> { route ->
        PasswordInputRoute(account = route.account)
    }
}
