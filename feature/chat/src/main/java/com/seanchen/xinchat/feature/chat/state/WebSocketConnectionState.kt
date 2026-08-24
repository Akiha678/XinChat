package com.seanchen.xinchat.feature.chat.state

sealed class WebSocketConnectionState {
    object Disconnected : WebSocketConnectionState()
    object Connecting : WebSocketConnectionState()
    object Connected : WebSocketConnectionState()
    data class Error(val message: String) : WebSocketConnectionState()
}