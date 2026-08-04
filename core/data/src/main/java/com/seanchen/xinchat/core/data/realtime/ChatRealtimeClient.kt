package com.seanchen.xinchat.core.data.realtime

import com.seanchen.xinchat.core.data.BuildConfig
import com.seanchen.xinchat.core.data.di.ApplicationScope
import com.seanchen.xinchat.core.data.model.RealtimeEvent
import com.seanchen.xinchat.core.data.network.ChatMessageDto
import com.seanchen.xinchat.core.data.network.toModel
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

@Singleton
internal class ChatRealtimeClient @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json,
    @param:ApplicationScope private val scope: CoroutineScope,
) {
    private val mutableEvents = MutableSharedFlow<RealtimeEvent>(extraBufferCapacity = 64)
    private val reconnectScheduled = AtomicBoolean(false)
    private var activeToken: String? = null
    private var webSocket: WebSocket? = null

    val events: SharedFlow<RealtimeEvent> = mutableEvents.asSharedFlow()

    @Synchronized
    fun connect(token: String) {
        if (activeToken == token && webSocket != null) return
        activeToken = token
        webSocket?.cancel()
        webSocket = client.newWebSocket(
            Request.Builder()
                .url(webSocketUrl())
                .header("Authorization", "Bearer $token")
                .build(),
            Listener(token),
        )
    }

    @Synchronized
    fun disconnect() {
        activeToken = null
        webSocket?.close(1000, "用户已退出登录")
        webSocket = null
    }

    private fun handleMessage(text: String) {
        val envelope = runCatching { json.decodeFromString<RealtimeEnvelopeDto>(text) }.getOrNull()
            ?: return
        val event = runCatching {
            when (envelope.type) {
                "message.created" -> RealtimeEvent.MessageCreated(
                    json.decodeFromJsonElement<ChatMessageDto>(envelope.data).toModel(),
                )
                "friend.request.changed" -> RealtimeEvent.FriendRequestChanged(
                    json.decodeFromJsonElement<RequestIdDto>(envelope.data).requestId,
                )
                "friendship.accepted" -> RealtimeEvent.FriendshipAccepted(
                    json.decodeFromJsonElement<ConversationIdDto>(envelope.data).conversationId,
                )
                "conversation.read" -> RealtimeEvent.ConversationRead(
                    json.decodeFromJsonElement<ConversationIdDto>(envelope.data).conversationId,
                )
                else -> null
            }
        }.getOrNull()
        if (event != null) mutableEvents.tryEmit(event)
    }

    private fun handleDisconnect(socket: WebSocket, token: String) {
        synchronized(this) {
            if (activeToken != token || webSocket !== socket) return
            webSocket = null
        }
        scheduleReconnect(token)
    }

    private fun scheduleReconnect(token: String) {
        if (activeToken != token || !reconnectScheduled.compareAndSet(false, true)) return
        scope.launch {
            delay(RECONNECT_DELAY_MILLIS)
            reconnectScheduled.set(false)
            if (activeToken == token) {
                connect(token)
            }
        }
    }

    private fun webSocketUrl(): String = BuildConfig.BASE_URL
        .replaceFirst("https://", "wss://")
        .replaceFirst("http://", "ws://") + "ws/chat"

    private inner class Listener(private val token: String) : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) = handleMessage(text)

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            handleDisconnect(webSocket, token)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            handleDisconnect(webSocket, token)
        }
    }

    @Serializable
    private data class RealtimeEnvelopeDto(val type: String, val data: JsonObject)

    @Serializable
    private data class ConversationIdDto(val conversationId: Long)

    @Serializable
    private data class RequestIdDto(val requestId: Long)

    private companion object {
        const val RECONNECT_DELAY_MILLIS = 3_000L
    }
}
