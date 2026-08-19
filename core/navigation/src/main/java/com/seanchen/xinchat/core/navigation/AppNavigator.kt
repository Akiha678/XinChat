package com.seanchen.xinchat.core.navigation

import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.data.state.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNavigator @Inject constructor(
    private val appState: AppState,
) {
    private val lock = Any()

    /**
     * 当前活跃的导航控制器
     */
    private var controller: NavigationController? = null

    /**
     * 控制器未注册时缓存的导航命令队列
     */
    private val pendingCommands = ArrayDeque<NavigationCommand>()

    /**
     * 缓存32元素
     */
    private val _resultEvents = MutableSharedFlow<ResultEvent>(extraBufferCapacity = 32)

    /**
     * 路由拦截器
     */
    private val routeInterceptor: RouteInterceptor = RouteInterceptor()

    /**
     * 注册导航控制器
     */
    fun attachController(navigationController: NavigationController) {
        // 上锁
        synchronized(lock) {
            controller = navigationController
            while (pendingCommands.isNotEmpty()) {
                pendingCommands.removeFirst().execute(navigationController)
            }
        }
    }

    /**
     * 注销导航控制器
     */
    fun detachController(navigationController: NavigationController) {
        synchronized(lock) {
            if (controller === navigationController) {
                controller = null
            }
        }
    }

    /**
     * 导航到指定路由
     */
    fun navigateTo(route: NavKey, navOptions: NavigationOptions? = null) {
        val targetRoute = resolveTargetRoute(route)
        executeOrEnqueue(NavigationCommand.NavigateTo(targetRoute, navOptions))
    }

    /**
     * 返回上一页
     */
    fun navigateBack() {
        executeOrEnqueue(NavigationCommand.NavigateUp)
    }

    /**
     * 返回上一页并携带类型安全结果
     */
    fun <T> popBackStackWithResult(key: NavigationResultKey<T>, result: T) {
        executeOrEnqueue(NavigationCommand.PopBackStackWithResult(key, result))
    }

    /**
     * 返回到指定路由
     */
    fun navigateBackTo(route: NavKey, inclusive: Boolean = false) {
        executeOrEnqueue(NavigationCommand.NavigateBackTo(route, inclusive))
    }

    /**
     * 监听某个ResultKey 对应的结果流
     */
    fun <T> resultEvents(key: NavigationResultKey<T>): Flow<T> {
        return _resultEvents
            .filter { it.key == key.key }
            .map { key.deserialize(it.rawValue) }
    }

    /**
     * 分发回传结果事件
     */
    internal fun <T> dispatchResult(key: NavigationResultKey<T>, result: T) {
        val rawValue = key.serialize(result)
        _resultEvents.tryEmit(ResultEvent(key = key.key, rawValue = rawValue))
    }

    /**
     * 执行导航命令
     */
    private fun executeOrEnqueue(command: NavigationCommand) {
        synchronized(lock) {
            val currentController = controller
            if (currentController != null) {
                command.execute(currentController)
            } else {
                pendingCommands.addLast(command)
            }
        }
    }

    /**
     * 解析最终跳转路由
     */
    private fun resolveTargetRoute(route: NavKey): NavKey {
        return if (routeInterceptor.requiresLogin(route) && !appState.isLoggedIn.value) {
            routeInterceptor.getLoginRoute()
        } else {
            route
        }
    }
}

/**
 * 导航回传结果事件
 */
private data class ResultEvent(
    val key: String,
    val rawValue: Any
)