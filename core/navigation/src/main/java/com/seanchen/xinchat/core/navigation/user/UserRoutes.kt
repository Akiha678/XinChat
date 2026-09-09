package com.seanchen.xinchat.core.navigation.user

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object UserRoutes {

    @Serializable
    data object Profile : NavKey
}