package com.seanchen.xinchat.core.navigation

import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.chat.ChatRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import kotlin.reflect.KClass

class RouteInterceptor {

    /**
     * 需要登录的路由类型集合
     */
    private val loginRequiredRouteTypes: Set<KClass<out NavKey>> = setOf(
        MainRoutes.Main::class,
        MainRoutes.Me::class,
        ChatRoutes.ChatList::class,
        ChatRoutes.ChatMessage::class,
        MainRoutes.Contact::class,
    )


    /**
     * 检查指定路由对象是否需要登录
     */
    fun requiresLogin(route: NavKey): Boolean {
        val routeClass = route::class
        return loginRequiredRouteTypes.contains(routeClass)
    }

    /**
     * 获取登录页面路由对象
     */
    fun getLoginRoute(): NavKey = AuthRoutes.Login
}
