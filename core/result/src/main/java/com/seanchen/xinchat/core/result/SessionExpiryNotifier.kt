package com.seanchen.xinchat.core.result

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 登录态失效通知器
 *
 * 服务端有两种表达方式：HTTP 401，以及 HTTP 200 加业务码 401。前者由网络层拦截器上报，
 * 后者由 [ResultHandler] 在解析响应时上报。数据层订阅后统一清理本地会话，
 * 避免每个 Feature 各自判断登录态失效。
 */
object SessionExpiryNotifier {

    /**
     * 服务端约定的登录态失效状态码，HTTP 状态码与业务码共用该值
     */
    const val UNAUTHORIZED_CODE = 401

    private val _events = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * 登录态失效事件，只做通知不携带数据
     */
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    /**
     * 上报登录态失效
     */
    fun notifyExpired() {
        _events.tryEmit(Unit)
    }
}
