package com.seanchen.xinchat.core.navigation

import androidx.navigation3.runtime.NavKey

/** 应用顶级目的地的稳定标识。 */
interface TopLevelNavKey : NavKey {
    val route: String
}
