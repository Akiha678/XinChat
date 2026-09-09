package com.seanchen.xinchat.core.navigation.user

import com.seanchen.xinchat.core.navigation.navigate

object UserNavigator {
    /**
     * 跳转到个人中心页
     */
    fun toProfile() {
        navigate(UserRoutes.Profile)
    }
}