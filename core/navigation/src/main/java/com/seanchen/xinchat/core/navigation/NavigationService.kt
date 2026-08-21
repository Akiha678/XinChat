package com.seanchen.xinchat.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow

object NavigationService {

    /**
     * 当前导航器实例
     */
    @Volatile
    private var navigator: AppNavigator? = null


    /**
     * 绑定导航器
     */
    fun bind(appNavigator: AppNavigator) {
        navigator = appNavigator
    }

    /**
     * 解绑导航器
     */
    fun unbind(appNavigator: AppNavigator) {
        if (navigator === appNavigator) {
            navigator = null
        }
    }

    /**
     * 获取当前导航器
     */
    fun requireNavigator(): AppNavigator {
        return navigator ?: error("AppNavigator is not bound")
    }

    /**
     * 跳转到目标路由
     */
    fun navigate(route: NavKey, navOptions: NavigationOptions? = null) {
        requireNavigator().navigateTo(route = route, navOptions = navOptions)
    }

    /**
     * 跳转到目标路由并关闭当前页面
     */
    fun navigateAndCloseCurrent(route: NavKey, currentRoute: NavKey) {
        val navOptions = NavigationOptions(
            popUpToRoute = currentRoute,
            inclusive = true,
            allowPopToEmpty = true
        )
        requireNavigator().navigateTo(route = route, navOptions = navOptions)
    }


    fun navigateWithPopUpTo(route: NavKey, popUpToRoute: NavKey, inclusive: Boolean = false) {
        val navOptions = NavigationOptions(
            popUpToRoute = popUpToRoute,
            inclusive = inclusive
        )
        requireNavigator().navigateTo(route = route, navOptions = navOptions)
    }

    /**
     * 返回上一页
     */
    fun navigateBack() {
        requireNavigator().navigateBack()
    }

    /**
     * 返回到指定路由
     */
    fun navigateBackTo(route: NavKey, inclusive: Boolean = false) {
        requireNavigator().navigateBackTo(route = route, inclusive = inclusive)
    }

    /**
     * 返回上一页并携带结果
     */
    fun <T> popBackStackWithResult(key: NavigationResultKey<T>, result: T) {
        requireNavigator().popBackStackWithResult(key = key, result = result)
    }

    /**
     * 返回上一页并携带结果
     */
    fun <T> navigateBackWithResult(key: NavigationResultKey<T>, result: T) {
        popBackStackWithResult(key = key, result = result)
    }

    fun <T> resultEvents(key: NavigationResultKey<T>): Flow<T> {
        return requireNavigator().resultEvents(key)
    }
}

/**
 * 跳转到目标路由
 */
fun navigate(route: NavKey, navOptions: NavigationOptions? = null) {
    NavigationService.navigate(route = route, navOptions = navOptions)
}


/**
 * 跳转到目标路由并关闭当前页面
 */
fun navigateAndCloseCurrent(route: NavKey, currentRoute: NavKey) {
    NavigationService.navigateAndCloseCurrent(route = route, currentRoute = currentRoute)
}

/**
 * 跳转到目标路由并按条件清理回退栈
 */
fun navigateWithPopUpTo(route: NavKey, popUpToRoute: NavKey, inclusive: Boolean = false) {
    NavigationService.navigateWithPopUpTo(
        route = route,
        popUpToRoute = popUpToRoute,
        inclusive = inclusive
    )
}

/**
 * 返回上一页
 */
fun navigateBack() {
    NavigationService.navigateBack()
}

/**
 * 返回到指定路由
 */
fun navigateBackTo(route: NavKey, inclusive: Boolean = false) {
    NavigationService.navigateBackTo(route = route, inclusive = inclusive)
}

/**
 * 返回上一页并携带结果
 */
fun <T> popBackStackWithResult(key: NavigationResultKey<T>, result: T) {
    NavigationService.popBackStackWithResult(key = key, result = result)
}

/**
 * 监听指定结果Key的结果流
 */
fun <T> resultEvents(key: NavigationResultKey<T>): Flow<T> {
    return NavigationService.resultEvents(key)
}