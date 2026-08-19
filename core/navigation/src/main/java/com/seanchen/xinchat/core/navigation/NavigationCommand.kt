package com.seanchen.xinchat.core.navigation

import androidx.navigation3.runtime.NavKey


internal sealed interface NavigationCommand {
    /**
     * 执行导航命令
     */
    fun execute(controller: NavigationController)

    /**
     * 导航到指定路由命令
     */
    data class NavigateTo(
        val route: NavKey,
        val navOptions: NavigationOptions?,
    ) : NavigationCommand {

        override fun execute(controller: NavigationController) {
            controller.navigateTo(route, navOptions)
        }
    }

    /**
     * 返回上一页命令
     */
    data object NavigateUp : NavigationCommand {
        override fun execute(controller: NavigationController) {
            controller.navigateBack()
        }
    }

    /**
     * 回退到指定路由命令
     */
    data class NavigateBackTo(
        val route: NavKey,
        val inclusive: Boolean
    ) : NavigationCommand {
        override fun execute(controller: NavigationController) {
            controller.navigateBackTo(route, inclusive)
        }
    }

    /**
     * 回退并回传结果命令
     */
    data class PopBackStackWithResult<T>(
        val key: NavigationResultKey<T>,
        val result: T
    ) : NavigationCommand {
        override fun execute(controller: NavigationController) {
            controller.popBackStackWithResult(key, result)
        }
    }
}