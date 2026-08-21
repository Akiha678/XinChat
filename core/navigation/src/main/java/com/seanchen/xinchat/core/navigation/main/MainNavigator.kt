package com.seanchen.xinchat.core.navigation.main

import com.seanchen.xinchat.core.navigation.navigate

object MainNavigator {
    /**
     * 跳转到主框架页
     */
    fun toMain() {
        navigate(MainRoutes.Main)
    }
}