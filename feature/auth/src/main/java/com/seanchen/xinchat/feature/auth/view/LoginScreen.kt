package com.seanchen.xinchat.feature.auth.view

import androidx.compose.runtime.Composable

/**
 * 登录入口路由。现已统一改造为全新样式的验证码登录页面。
 */
@Composable
internal fun LoginRoute() {
    SmsLoginRoute()
}

//@Composable
//internal fun LoginScreen() {
//    SmsLoginScreen()
//}
