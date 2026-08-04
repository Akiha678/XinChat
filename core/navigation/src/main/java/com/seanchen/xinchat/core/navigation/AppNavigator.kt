package com.seanchen.xinchat.core.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * 统一处理应用导航操作，可保存的导航状态仍由 Compose 持有。
 *
 * 每个顶级目的地拥有独立返回栈，切换标签时能够保留界面状态、可保存状态以及后续加入的
 * 内部目的地。
 */
@Stable
class AppNavigator(
    private val selectedRoute: MutableState<String>,
    private val backStacks: Map<TopLevelNavKey, NavBackStack<NavKey>>,
    val startDestination: TopLevelNavKey,
) {
    init {
        require(backStacks.isNotEmpty()) { "至少需要一个顶级目的地" }
        require(startDestination in backStacks) { "起始目的地必须拥有独立返回栈" }
        require(backStacks.keys.map(TopLevelNavKey::route).distinct().size == backStacks.size) {
            "顶级目的地的路由必须唯一"
        }
        require(backStacks.keys.any { it.route == selectedRoute.value }) {
            "当前路由必须对应一个顶级目的地"
        }
    }

    val currentDestination: TopLevelNavKey
        get() = backStacks.keys.first { it.route == selectedRoute.value }

    val currentBackStack: NavBackStack<NavKey>
        get() = backStackFor(currentDestination)

    val canNavigateBack: Boolean
        get() = currentBackStack.size > 1

    fun backStackFor(destination: TopLevelNavKey): NavBackStack<NavKey> =
        requireNotNull(backStacks[destination]) {
            "路由 ${destination.route} 未注册返回栈"
        }

    fun navigateTo(destination: TopLevelNavKey) {
        require(destination in backStacks) {
            "路由 ${destination.route} 未注册返回栈"
        }
        selectedRoute.value = destination.route
    }

    fun navigateTo(key: NavKey) {
        currentBackStack.add(key)
    }

    fun pop(): Boolean {
        if (!canNavigateBack) return false
        currentBackStack.removeAt(currentBackStack.lastIndex)
        return true
    }

    fun navigateBackToStart(): Boolean {
        if (canNavigateBack) return pop()
        if (currentDestination == startDestination) return false
        navigateTo(startDestination)
        return true
    }
}
