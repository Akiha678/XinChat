package com.seanchen.xinchat.feature.chat.state

/**
 * WebSocket连接状态
 */
sealed class WebSocketConnectionState {
    /** 日志与验收契约使用的稳定标签（大写），刻意不依赖类名 */
    abstract val label: String

    // 断开连接
    object Disconnected : WebSocketConnectionState() {
        override val label: String = "DISCONNECTED"
    }
    // 连接中
    object Connecting : WebSocketConnectionState() {
        override val label: String = "CONNECTING"
    }
    // 已连接
    object Connected : WebSocketConnectionState() {
        override val label: String = "CONNECTED"
    }
    // 链路异常断开，正在按退避策略重连
    object Reconnecting : WebSocketConnectionState() {
        override val label: String = "RECONNECTING"
    }
    // 失败
    data class Error(val message: String) : WebSocketConnectionState() {
        override val label: String = "ERROR"
    }
}