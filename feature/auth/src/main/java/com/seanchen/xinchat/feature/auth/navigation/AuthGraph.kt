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
     * 验证码登录界面
     */
    entry<AuthRoutes.CodeLogin> {
        CodeLoginRoute()
    }

    /**
     * 验证码输入界面
     */
    entry<AuthRoutes.CodeVerify> { route ->
        CodeVerifyRoute(email = route.email)
    }

    /**
     * 密码登录界面
     */
    entry<AuthRoutes.PasswordLogin> {
        PasswordLoginRoute()
    }

    /**
     * 密码输入界面
     */
    entry<AuthRoutes.PasswordInput> { route ->
        PasswordInputRoute(email = route.email)
    }
}
