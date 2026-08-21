package com.seanchen.xinchat

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.seanchen.xinchat.core.navigation.AppNavigator
import com.seanchen.xinchat.core.navigation.NavigationService
import com.seanchen.xinchat.core.navigation.TopLevelNavKey
import com.seanchen.xinchat.core.navigation.createBackStackNavigationController
import com.seanchen.xinchat.feature.auth.navigation.authGraph
import com.seanchen.xinchat.feature.main.navigation.mainGraph
import kotlin.collections.listOf


private const val NAV_ANIMATION_DURATION = 300

private val NAV_ANIMATION_SPEC: FiniteAnimationSpec<IntOffset> =
    tween(durationMillis = NAV_ANIMATION_DURATION)

@Composable
fun AppNavHost(
    navigator: AppNavigator,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack()

    val navigationController = remember(backStack, navigator){
        createBackStackNavigationController(backStack, navigator)
    }

    DisposableEffect(navigationController) {
        // 绑定到 AppNavigator，接收全局导航命令
        navigator.attachController(navigationController)
        // 绑定到全局导航服务，支持业务层直接调用navigate
        NavigationService.bind(navigator)
        onDispose {
            NavigationService.unbind(navigator)
            navigator.detachController(navigationController)
        }
    }

    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = { navigationController.navigateBack() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = { createForwardTransition() },
            popTransitionSpec = { createBackwardTransition() },
            predictivePopTransitionSpec = { createBackwardTransition() },
            entryProvider = appEntryProvider(this@SharedTransitionLayout)
        )
    }
}

/**
 * 创建前进导航动画
 */
private fun createForwardTransition() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = NAV_ANIMATION_SPEC,
) togetherWith slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = NAV_ANIMATION_SPEC
)

/**
 * 创建返回导航动画
 */
private fun createBackwardTransition() = slideInHorizontally (
    initialOffsetX = { -it },
    animationSpec = NAV_ANIMATION_SPEC,
) togetherWith slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = NAV_ANIMATION_SPEC
)

private fun appEntryProvider(sharedTransitionScope: SharedTransitionScope) = entryProvider {
    mainGraph(sharedTransitionScope)
    authGraph()
}