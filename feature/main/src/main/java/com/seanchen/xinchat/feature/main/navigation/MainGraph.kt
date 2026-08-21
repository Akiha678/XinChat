package com.seanchen.xinchat.feature.main.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.feature.main.view.MainRoute

fun EntryProviderScope<NavKey>.mainGraph(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<MainRoutes.Main> {
        MainRoute(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = LocalNavAnimatedContentScope.current,
        )
    }
}