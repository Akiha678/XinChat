package com.seanchen.xinchat.feature.chat.state

/**
 * WebSocket连接状态
 */
sealed class WebSocketConnectionState {
    // 断开连接
    object Disconnected : WebSocketConnectionState()
    // 连接中
    object Connecting : WebSocketConnectionState()
    // 已连接
    object Connected : WebSocketConnectionState()
    // 失败
    data class Error(val message: String) : WebSocketConnectionState()
}