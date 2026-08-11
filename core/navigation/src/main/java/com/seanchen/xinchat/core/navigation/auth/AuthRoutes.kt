package com.seanchen.xinchat.core.navigation.auth

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object AuthRoutes {
    @Serializable
    data object AccountLogin : NavKey
}