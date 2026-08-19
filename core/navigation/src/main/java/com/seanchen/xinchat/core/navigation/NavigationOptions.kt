package com.seanchen.xinchat.core.navigation

import androidx.navigation3.runtime.NavKey

data class NavigationOptions(
    val popUpToRoute: NavKey? = null,
    val inclusive: Boolean = false,
    val allowPopToEmpty: Boolean = false,
)
