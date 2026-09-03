package com.seanchen.xinchat.core.navigation.main

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 主模块路由
 */
object MainRoutes {
    @Serializable
    data object Main : NavKey

    @Serializable
    data object Contact : NavKey

    @Serializable
    data object AddFriend : NavKey

    @Serializable
    data object Me : NavKey
}
