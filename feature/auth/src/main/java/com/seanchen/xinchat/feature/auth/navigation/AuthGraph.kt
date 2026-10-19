package com.seanchen.xinchat.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.feature.auth.view.PasswordLoginRoute
import com.seanchen.xinchat.feature.auth.view.PasswordInputRoute
import com.seanchen.xinchat.feature.auth.view.CodeVerifyRoute
import com.seanchen.xinchat.feature.auth.view.CodeLoginRoute

fun EntryProviderScope<NavKey>.authGraph() {
    /**
     * 验证码登录界面（第一步：输入邮箱）
     */
    entry<AuthRoutes.SmsLogin> {
        CodeLoginRoute()
    }

    /**
     * 验证码输入界面（第二步：输入验证码）
     */
    entry<AuthRoutes.SmsCodeVerify> { route ->
        CodeVerifyRoute(email = route.email)
    }

    /**
     * 密码登录界面（第一步：输入邮箱）
     */
    entry<AuthRoutes.AccountLogin> {
        PasswordLoginRoute()
    }

    /**
     * 密码输入界面（第二步：输入密码）
     */
    entry<AuthRoutes.PasswordInput> { route ->
        PasswordInputRoute(email = route.email)
    }
}
