package com.seanchen.xinchat.feature.user.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.core.navigation.TopLevelNavKey
import com.seanchen.xinchat.feature.user.view.MeRoute
import com.seanchen.xinchat.feature.user.view.ProfileRoute
import kotlinx.serialization.Serializable

object ProfileRoutes {
    @Serializable
    data object Profile : TopLevelNavKey {
        override val route: String = "profile"
    }
}

fun EntryProviderScope<NavKey>.profileGraph(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<ProfileRoutes.Profile> {
        ProfileRoute(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = LocalNavAnimatedContentScope.current,
        )
    }
}

fun EntryProviderScope<NavKey>.meGraph() {
    entry<MainRoutes.Me> {
        MeRoute()
    }
}
