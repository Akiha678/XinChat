package com.seanchen.xinchat.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun createBackStackNavigationController(
    backStack: NavBackStack<NavKey>,
    navigator: AppNavigator
): NavigationController {
    return BackStackNavigationController(backStack = backStack, navigator = navigator)
}

private class BackStackNavigationController(
    /**
     * 回退栈
     */
    private val backStack: NavBackStack<NavKey>,

    /**
     * 导航管理器
     */
    private val navigator: AppNavigator
): NavigationController {
    /**
     * 导航到目标页面
     */
    override fun navigateTo(route: NavKey, navOptions: NavigationOptions?) {
        val popUpToRoute = navOptions?.popUpToRoute
        if (popUpToRoute != null) {
            backStack.popUpTo(
                route = popUpToRoute,
                inclusive = navOptions.inclusive,
                allowPopToEmpty = navOptions.allowPopToEmpty
            )
        }
        backStack.add(route)
    }

    /**
     * 返回上一页
     */
    override fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    /**
     * 返回到指定页面
     */
    override fun navigateBackTo(route: NavKey, inclusive: Boolean) {
        backStack.popUpTo(route = route, inclusive = inclusive)
    }

    /**
     * 回退并携带结果
     */
    override fun <T> popBackStackWithResult(key: NavigationResultKey<T>, result: T) {
        navigator.dispatchResult(key = key, result = result)
        navigateBack()
    }
}

/**
 * BackStack 按路由弹栈
 */
private fun NavBackStack<NavKey>.popUpTo(
    route: NavKey,
    inclusive: Boolean,
    allowPopToEmpty: Boolean = false,
) {
    val targetIndex = indexOfLast { it == route }
    if (targetIndex == -1) return

    val removeFromIndex = if (inclusive) targetIndex else targetIndex + 1
    if (removeFromIndex >= size) return

    if (removeFromIndex == 0) {
        if (allowPopToEmpty) {
            clear()
        } else if (size > 1) {
            subList(1, size).clear()
        }
        return
    }
    subList(removeFromIndex, size).clear()
}

