package com.seanchen.xinchat.core.navigation.auth

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object AuthRoutes {

    /**
     * 登录主页路由
     */
    @Serializable
    data object Login : NavKey

    /**
     * 账号密码
     */
    @Serializable
    data object AccountLogin : NavKey
}