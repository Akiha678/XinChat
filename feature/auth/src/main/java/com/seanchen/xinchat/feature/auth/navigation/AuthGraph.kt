package com.seanchen.xinchat.feature.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.feature.auth.view.LoginRoute
import com.seanchen.xinchat.feature.auth.view.AccountLoginRoute
import com.seanchen.xinchat.feature.auth.view.RegisterRoute
import com.seanchen.xinchat.feature.auth.view.SmsLoginRoute
import com.seanchen.xinchat.feature.auth.view.SmsLoginScreen

fun EntryProviderScope<NavKey>.authGraph() {
    /**
     * 登录界面
     */
    entry<AuthRoutes.Login> {
        LoginRoute()
    }

    /**
     * 账号密码登录
     */
    entry<AuthRoutes.AccountLogin>{
        AccountLoginRoute()
    }

    /**
     * 验证码登录
     */
    entry<AuthRoutes.SmsLogin> {
        SmsLoginRoute()
    }

    /**
     * 注册界面
     */
    entry<AuthRoutes.Register> {
        RegisterRoute()
    }
}
