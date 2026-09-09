package com.seanchen.xinchat.feature.user.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.seanchen.xinchat.core.navigation.user.UserRoutes
import com.seanchen.xinchat.feature.user.view.ProfileRoute

fun EntryProviderScope<NavKey>.userGraph(
    sharedTransitionScope: SharedTransitionScope
){
    entry<UserRoutes.Profile> {
        ProfileRoute(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = LocalNavAnimatedContentScope.current
        )
    }
}
