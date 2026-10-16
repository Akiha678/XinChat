package com.seanchen.xinchat.feature.chat.util

import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.util.log.LogUtils
import com.seanchen.xinchat.feature.chat.BuildConfig
import com.seanchen.xinchat.feature.chat.state.WebSocketConnectionState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.random.Random

private const val TAG = "WebSocketManager"
private const val SOCKET_IO_PATH = "socket.io/?EIO=4&transport=websocket"

/** 契约日志前缀：验收正则按字面量匹配，不得改动。注意**不含尾随空格** —— 消息模板统一写成 "$WS_LOG_PREFIX xxx"，多一个空格就会让契约正则失配 */
private const val WS_LOG_PREFIX = "[XinChat-WS]"

// 心跳保活与重连的默认参数，取值即《01-WebSocket-心跳与重连协议规范》的规格值
private const val DEFAULT_PING_INTERVAL_MS = 30_000L
private const val DEFAULT_PONG_TIMEOUT_MS = 10_000L
private const val DEFAULT_RETRY_BASE_DELAY_MS = 1_000L
private const val DEFAULT_RETRY_MULTIPLIER = 2.0
private const val DEFAULT_RETRY_MAX_DELAY_MS = 30_000L
private const val DEFAULT_MAX_RETRIES = 5

/**
 * 心跳保活与指数退避重连的策略参数。
 *
 * 生产默认值即协议规格值；单测传入毫秒级短间隔，把 30s 级行为压到秒级，
 * 从而在不牺牲生产默认值的前提下做确定性验证。
 */
data class WsConfig(
    val pingIntervalMs: Long = DEFAULT_PING_INTERVAL_MS,
    val pongTimeoutMs: Long = DEFAULT_PONG_TIMEOUT_MS,
    val retryBaseDelayMs: Long = DEFAULT_RETRY_BASE_DELAY_MS,
    val retryMultiplier: Double = DEFAULT_RETRY_MULTIPLIER,
    val retryMaxDelayMs: Long = DEFAULT_RETRY_MAX_DELAY_MS,
    val maxRetries: Int = DEFAULT_MAX_RETRIES,
)

/**
 * 管理单个页面持有的 WebSocket 实时连接。
 *
 * 连接被服务端关闭或网络失败时，只要页面仍期望保持连接（未调用 [disconnect]），
 * 就会按退避策略自动重连，避免会话列表页/聊天页长时间静默断线后收不到新消息。
 */
class WebSocketManager(
    private val baseUrl: String = BuildConfig.BASE_URL,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val config: WsConfig = WsConfig(),
    private val random: Random = Random.Default,
) {
    private val _connectionState = MutableStateFlow<WebSocketConnectionState>(WebSocketConnectionState.Disconnected)
    val connectionState: StateFlow<WebSocketConnectionState> = _connectionState.asStateFlow()

    private var webSocket: WebSocket? = null

    /**
     * 是否期望保持连接；调用 [disconnect] 后置为 false，不再自动重连
     */
    private var keepConnected = false

    /**
     * 最近一次连接使用的鉴权信息与作用域，自动重连时复用
     */
    private var connectToken: String = ""
    private var connectScope: CoroutineScope? = null

    /**
     * 待执行的重连任务
     */
    private var retryJob: Job? = null

    /**
     * 心跳保活任务；仅在 CONNECTED 期间存活
     */
    private var heartbeatJob: Job? = null

    /**
     * 当前等待的 PONG 信号；发出 Ping 时创建，收到 PONG 或超时后置空
     */
    private var pongSignal: CompletableDeferred<Unit>? = null

    /**
     * 重试计数器；退避延迟按 [WsConfig] 的指数公式计算
     */
    private var retryCount = 0

    private var onMessageReceived: ((Msg) -> Unit)? = null
    private var onConnectionStateChanged: ((WebSocketConnectionState) -> Unit)? = null

    /**
     * 保护连接状态切换的锁；OkHttp 回调线程与调用方线程共用
     */
    private val lock = Any()

    /**
     * 协程兜底异常处理：断网/重连期间的异常不得逃逸导致进程 Crash
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, t ->
        LogUtils.e(TAG, "$WS_LOG_PREFIX 协程异常: ${t.message}", t)
    }

    /**
     * 设置消息接收回调
     */
    fun setOnMessageReceived(callback: (Msg) -> Unit) {
        onMessageReceived = callback
    }

    /**
     * 设置连接状态变化回调
     */
    fun setOnConnectionStateChanged(callback: (WebSocketConnectionState) -> Unit) {
        onConnectionStateChanged = callback
    }

    /**
     * 建立WebSocket连接。已连接或正在连接时重复调用只会更新鉴权信息，不会创建第二条连接。
     */
    fun connect(token: String, scope: CoroutineScope) {
        synchronized(lock) {
            keepConnected = true
            connectToken = token
            connectScope = scope

            val state = _connectionState.value
            if (state == WebSocketConnectionState.Connected ||
                state == WebSocketConnectionState.Connecting
            ) {
                LogUtils.d(TAG, "WebSocket已连接或正在连接中，忽略重复连接请求")
                return
            }

            retryCount = 0
            retryJob?.cancel()
            retryJob = null
            openConnection()
        }
    }

    /**
     * 真正发起一次连接；需持锁调用。
     */
    companion object {
        private val sharedClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .pingInterval(0, TimeUnit.SECONDS) // 禁用OkHttp的自动ping
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS) // 增加读取超时时间，避免长时间无消息导致断开
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
        }
    }

    private fun openConnection() {
        updateConnectionState(WebSocketConnectionState.Connecting)
        val scope = connectScope ?: return
        val token = connectToken
        if (token.isBlank()) {
            LogUtils.e(TAG, "缺少鉴权Token，无法建立WebSocket连接")
            updateConnectionState(WebSocketConnectionState.Error("登录状态已失效"))
            return
        }

        scope.launch(ioDispatcher + exceptionHandler) {
            LogUtils.d(TAG, "开始建立WebSocket连接")
            LogUtils.d(TAG, "用户Token: ${token.take(15)}...")

            val request = Request.Builder()
                .url(createWebSocketUrl())
                .build()

            val authorizedRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()

            webSocket = sharedClient.newWebSocket(authorizedRequest, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    LogUtils.d(TAG, "WebSocket连接成功: ${response.code}")
                    synchronized(lock) {
                        retryCount = 0
                    }

                    // 发送认证消息
                    val authMessage = """40/chat,{"token":"$token"}"""
                    LogUtils.d(TAG, "发送认证消息: ${authMessage.take(20)}...")
                    webSocket.send(authMessage)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    LogUtils.d(TAG, "收到WebSocket消息: ${text}...")
                    // 如果收到心跳包，立即响应
                    if (text == "2") {
                        LogUtils.d(TAG, "收到心跳包")
                        val success = webSocket.send("3")
                        // 检查心跳响应是否发送成功
                        if (success) {
                            LogUtils.d(TAG, "心跳响应发送成功")
                        } else {
                            LogUtils.e(TAG, "心跳响应发送失败")
                        }
                    }
                    // 应用层心跳应答：完成当前 Ping 的等待信号
                    if (text.contains("\"PONG\"")) {
                        pongSignal?.complete(Unit)
                    }
                    handleWebSocketMessage(text)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    LogUtils.e(TAG, "WebSocket连接失败: ${t.message}", t)
                    stopHeartbeat()
                    updateConnectionState(WebSocketConnectionState.Error(t.message ?: "连接错误"))
                    synchronized(lock) {
                        scheduleReconnect()
                    }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    LogUtils.d(TAG, "WebSocket连接关闭: code=$code, reason=$reason")
                    stopHeartbeat()
                    updateConnectionState(WebSocketConnectionState.Disconnected)
                    // 连接被服务端关闭（重复登录被顶掉、空闲超时等）后尝试恢复连接
                    synchronized(lock) {
                        scheduleReconnect()
                    }
                }
            })
        }
    }

    /**
     * 计算第 [attempt] 次重连的退避延迟：min(base * multiplier^(attempt-1), max) + 抖动。
     *
     * 抖动上界取封顶值的 50%，用于打散多客户端的重连时刻，避免惊群。
     */
    private fun computeBackoffDelayMs(attempt: Int): Long {
        val raw = config.retryBaseDelayMs * config.retryMultiplier.pow((attempt - 1).toDouble())
        val capped = minOf(raw, config.retryMaxDelayMs.toDouble()).toLong()
        val jitterBound = (capped / 2).coerceAtLeast(1L)
        return capped + random.nextLong(0L, jitterBound)
    }

    /**
     * 连接失败或被关闭后按指数退避策略安排一次重连；需持锁调用。
     */
    private fun scheduleReconnect() {
        if (!keepConnected) {
            return
        }
        if (retryCount >= config.maxRetries) {
            LogUtils.e(TAG, "$WS_LOG_PREFIX 重连次数达到上限，停止自动重连")
            updateConnectionState(WebSocketConnectionState.Error("连接已断开，请稍后重试"))
            return
        }
        retryCount++
        val attempt = retryCount
        val scope = connectScope ?: return
        val delayMs = computeBackoffDelayMs(attempt)
        retryJob?.cancel()
        updateConnectionState(WebSocketConnectionState.Reconnecting)
        retryJob = scope.launch(ioDispatcher + exceptionHandler) {
            LogUtils.d(TAG, "$WS_LOG_PREFIX Reconnect attempt $attempt/${config.maxRetries} in ${delayMs}ms")
            delay(delayMs)
            synchronized(lock) {
                if (keepConnected && _connectionState.value != WebSocketConnectionState.Connecting) {
                    openConnection()
                }
            }
        }
    }

    /**
     * 启动心跳保活循环；进入 CONNECTED 时调用。
     *
     * delay 与 withTimeoutOrNull 全部运行在 [ioDispatcher] 上，主线程零阻塞。
     */
    private fun startHeartbeat(scope: CoroutineScope) {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch(ioDispatcher + exceptionHandler) {
            while (isActive && keepConnected) {
                delay(config.pingIntervalMs)
                val timestamp = System.currentTimeMillis()
                val payload = """{"type":"PING","timestamp":$timestamp}"""
                val sent = webSocket?.send(payload) ?: false
                if (!sent) {
                    LogUtils.e(TAG, "$WS_LOG_PREFIX Ping send failed")
                    onLinkDead()
                    return@launch
                }
                LogUtils.d(TAG, "$WS_LOG_PREFIX Ping sent at $timestamp")

                val signal = CompletableDeferred<Unit>()
                pongSignal = signal
                val pong = withTimeoutOrNull(config.pongTimeoutMs) { signal.await() }
                pongSignal = null
                if (pong == null) {
                    LogUtils.e(TAG, "$WS_LOG_PREFIX Pong timeout after ${config.pongTimeoutMs}ms, reconnecting")
                    onLinkDead()
                    return@launch
                }
                LogUtils.d(TAG, "$WS_LOG_PREFIX Pong received")
            }
        }
    }

    /**
     * 停止心跳保活循环并清空待应答信号。
     */
    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
        pongSignal = null
    }

    /**
     * 判定链路假死：停心跳、关闭旧 socket、按退避策略安排重连。
     */
    private fun onLinkDead() {
        stopHeartbeat()
        runCatching { webSocket?.close(1000, "心跳超时") }
        webSocket = null
        synchronized(lock) {
            scheduleReconnect()
        }
    }

    private fun handleWebSocketMessage(text: String) {
        try {
            when {
                // 连接成功消息: 40/cs,{"sid":"708fb2ed-6d08-445c-9264-c57c521eb3f7"}
                text.startsWith("40/chat,") -> {
                    LogUtils.d(TAG, "WebSocket认证成功")
                    updateConnectionState(WebSocketConnectionState.Connected)
                }

                // 握手消息: 0{"sid":"708fb2ed-6d08-445c-9264-c57c521eb3f7","upgrades":[],"pingInterval":25000,"pingTimeout":6000000}
                text.startsWith("0{") -> {
                    LogUtils.d(TAG, "WebSocket握手成功")
                    // 握手成功，不做特殊处理
                }

                // 心跳消息: 2
                // 注意：这个处理已经移到onMessage中直接处理，以确保及时响应
                text == "2" -> {
                    // 心跳包的处理已移至onMessage回调中
                    // 这里不再需要额外处理
                }

                // 消息通知: 42["msg",{消息内容}]
                text.startsWith("42[\"msg\",") -> {
                    val messageContent = text.substringAfter("42[\"msg\",").substringBeforeLast("]")
                    val message = Json.decodeFromString<Msg>(messageContent)
                    LogUtils.d(TAG, "收到消息通知(42): id=${message.id}")
                    onMessageReceived?.invoke(message)
                }

                // 消息通知: 42/chat,["msg",{消息内容}]
                text.startsWith("42/chat,[\"msg\",") -> {
                    val messageContent =
                        text.substringAfter("42/chat,[\"msg\",").substringBeforeLast("]")
                    val message = Json.decodeFromString<Msg>(messageContent)
                    LogUtils.d(TAG, "收到消息通知(42/chat): id=${message.id}")
                    onMessageReceived?.invoke(message)
                }

                // 连接成功消息: 42/cs,["message","连接成功"]
                text.contains("42/chat,[\"message\",\"连接成功\"]") -> {
                    LogUtils.d(TAG, "收到连接成功消息: $text")
                    updateConnectionState(WebSocketConnectionState.Connected)
                }

                // 其他连接状态消息
                text.startsWith("42/chat,[\"message\",") -> {
                    LogUtils.d(TAG, "收到连接状态消息: $text")
                    // 其他连接状态消息，已经在上一个条件处理了连接成功的情况
                }

                else -> {
                    LogUtils.d(TAG, "收到未处理的消息类型: $text")
                }
            }
        } catch (e: Exception) {
            LogUtils.e(TAG, "解析WebSocket消息失败", e)
        }
    }


    /**
     * 发送消息
     */
    fun sendMessage(sessionId: Long, content: String, type: String = "text"): Boolean {
        if (_connectionState.value != WebSocketConnectionState.Connected) {
            LogUtils.e(TAG, "发送消息失败：WebSocket未连接")
            return false
        }

        val sendMessage =
            """42/chat,["send",{"sessionId":$sessionId,"content":{"type":"$type","data":"$content"}}]"""
        LogUtils.d(TAG, "发送消息: ${sendMessage.take(50)}")

        val success = webSocket?.send(sendMessage) ?: false
        if (!success) {
            LogUtils.e(TAG, "消息发送失败")
            updateConnectionState(WebSocketConnectionState.Error("消息发送失败"))
        } else {
            LogUtils.d(TAG, "消息发送成功")
        }

        return success
    }

    /**
     * 断开WebSocket连接并取消后续自动重连
     */
    fun disconnect() {
        synchronized(lock) {
            keepConnected = false
            retryJob?.cancel()
            retryJob = null
            stopHeartbeat()

            LogUtils.d(TAG, "断开WebSocket连接")
            webSocket?.close(1000, "正常关闭")
            webSocket = null
            updateConnectionState(WebSocketConnectionState.Disconnected)
        }
    }

    /**
     * 更新连接状态：状态真正变化时打印契约日志，并在进入 CONNECTED 时启动心跳保活。
     */
    private fun updateConnectionState(state: WebSocketConnectionState) {
        val changed = _connectionState.value != state
        _connectionState.value = state
        if (changed) {
            LogUtils.d(TAG, "$WS_LOG_PREFIX Connection status: ${state.label}")
            if (state == WebSocketConnectionState.Connected) {
                connectScope?.let { startHeartbeat(it) }
            }
        }
        onConnectionStateChanged?.invoke(state)
    }

    /**
     * 获取当前连接状态
     */
    fun isConnected(): Boolean {
        return _connectionState.value == WebSocketConnectionState.Connected
    }

    private fun createWebSocketUrl(): String {
        val normalized = baseUrl
            .replaceFirst("https://", "wss://")
            .replaceFirst("http://", "ws://")
            .trimEnd('/')
        return "$normalized/$SOCKET_IO_PATH"
    }
}
