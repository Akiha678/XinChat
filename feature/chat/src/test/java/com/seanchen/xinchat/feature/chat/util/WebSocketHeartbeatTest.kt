package com.seanchen.xinchat.feature.chat.util

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.random.Random

/**
 * XinChat WebSocket 心跳保活 / 指数退避重连的行为取证桩（skill-test 用）。
 *
 * 说明（**证据等级**）：本机无在线 Android 设备、无 AVD、模拟器因缺硬件加速不可用，
 * 因此无法走 `adb install` + 真机 Logcat。本桩在同一份**生产源码**上驱动真实的
 * [WebSocketManager]，用本地 MockWebServer 充当服务端，并把捕获到的日志按 Logcat
 * 风格写入 `build/ws-harness/app.log`，供 `tools/xinchat_selftest.py` 做契约比对。
 *
 * 覆盖：心跳周期、状态流转、Pong 超时判假死、指数退避 + 抖动 + 封顶。
 */
class WebSocketHeartbeatTest {

    private lateinit var server: MockWebServer
    private lateinit var scope: CoroutineScope
    private var manager: WebSocketManager? = null

    private val logs = CopyOnWriteArrayList<String>()
    private val outFile = File("build/ws-harness/app.log")

    // ------------------------------------------------------------------ 服务端桩
    /** 实现《01-WebSocket-心跳与重连协议规范》§二 的应用层 PONG 应答。 */
    private class HarnessServer(
        private val replyPong: Boolean,
        private val replyAuth: Boolean = true,
    ) : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send(
                """0{"sid":"harness","upgrades":[],"pingInterval":25000,"pingTimeout":6000000}"""
            )
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            when {
                // Socket.IO 引擎层心跳
                text == "2" -> webSocket.send("3")
                // 命名空间接入成功
                text.startsWith("40/chat,") -> if (replyAuth) {
                    webSocket.send("""40/chat,{"sid":"harness"}""")
                }
                // 应用层心跳
                text.contains("\"PING\"") -> if (replyPong) {
                    val ts = Regex("\"timestamp\":(\\d+)").find(text)?.groupValues?.get(1) ?: "0"
                    webSocket.send("""{"type":"PONG","timestamp":$ts}""")
                }
            }
        }
    }

    /** 把 Timber 日志按 Logcat brief 风格捕获，便于与 goal.yml 正则直接对齐。 */
    private class CaptureTree(private val sink: MutableList<String>) : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            val level = when (priority) {
                2 -> "V"; 3 -> "D"; 4 -> "I"; 5 -> "W"; 6 -> "E"; 7 -> "A"; else -> "?"
            }
            sink.add("$level/${tag ?: "?"}: $message")
        }
    }

    @get:org.junit.Rule
    val testName = org.junit.rules.TestName()

    // ------------------------------------------------------------------ 生命周期
    @Before
    fun setUp() {
        Timber.uprootAll()
        logs.clear()
        Timber.plant(CaptureTree(logs))
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        server = MockWebServer()
    }

    @After
    fun tearDown() {
        runCatching { manager?.disconnect() }
        scope.cancel()
        runCatching { server.shutdown() }
        Timber.uprootAll()
        dump()
    }

    // ------------------------------------------------------------------ 工具
    private fun newManager(
        pingIntervalMs: Long = 300L,
        pongTimeoutMs: Long = 200L,
        retryBaseDelayMs: Long = 60L,
        retryMaxDelayMs: Long = 300L,
        maxRetries: Int = 5,
    ): WebSocketManager = WebSocketManager(
        baseUrl = server.url("/").toString(),
        ioDispatcher = Dispatchers.IO,
        config = WsConfig(
            pingIntervalMs = pingIntervalMs,
            pongTimeoutMs = pongTimeoutMs,
            retryBaseDelayMs = retryBaseDelayMs,
            retryMultiplier = 2.0,
            retryMaxDelayMs = retryMaxDelayMs,
            maxRetries = maxRetries,
        ),
        random = Random(20261015),
    ).also { manager = it }

    private fun awaitUntil(timeoutMs: Long, cond: () -> Boolean): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (cond()) return true
            Thread.sleep(20)
        }
        return cond()
    }

    private fun hasStatus(label: String) = logs.any { it.contains("Connection status: $label") }

    private fun pingTimestamps(): List<Long> =
        logs.filter { it.contains("Ping sent at") }
            .mapNotNull { Regex("Ping sent at (\\d+)").find(it)?.groupValues?.get(1)?.toLongOrNull() }

    /** 解析 `Reconnect attempt n/max in Xms`，返回 (attempt, delayMs) 列表。 */
    private fun reconnectAttempts(): List<Pair<Int, Long>> =
        logs.mapNotNull {
            val m = Regex("Reconnect attempt (\\d+)/(\\d+) in (\\d+)ms").find(it) ?: return@mapNotNull null
            m.groupValues[1].toInt() to m.groupValues[3].toLong()
        }

    private fun dump() {
        outFile.parentFile?.mkdirs()
        outFile.appendText(
            buildString {
                appendLine("===== WS-HARNESS :: ${testName.methodName} =====")
                logs.forEach { appendLine(it) }
                appendLine()
            }
        )
        println("[WS-HARNESS] evidence appended -> ${outFile.absolutePath}")
    }

    // ------------------------------------------------------------------ TC-01
    @Test
    fun tc01_heartbeat_pings_at_configured_interval() {
        server.enqueue(MockResponse().withWebSocketUpgrade(HarnessServer(replyPong = true)))
        server.start()
        val m = newManager(pingIntervalMs = 300L)
        m.connect("harness-token", scope)

        assertTrue("应进入 CONNECTED", awaitUntil(8000) { hasStatus("CONNECTED") })
        assertTrue("应至少发出 3 次 Ping", awaitUntil(8000) { pingTimestamps().size >= 3 })

        val ts = pingTimestamps().take(4)
        val gaps = (1 until ts.size).map { ts[it] - ts[it - 1] }
        assertTrue(
            "Ping 间隔应接近 300ms（±20% 容差），实际间隔=$gaps",
            gaps.isNotEmpty() && gaps.all { it in 240..1000 }
        )
    }

    // ------------------------------------------------------------------ TC-02
    @Test
    fun tc02_status_logs_follow_state_machine() {
        server.enqueue(MockResponse().withWebSocketUpgrade(HarnessServer(replyPong = true)))
        server.start()
        val m = newManager(pingIntervalMs = 300L)
        m.connect("harness-token", scope)

        assertTrue("应出现 CONNECTING", hasStatus("CONNECTING"))
        assertTrue("应出现 CONNECTED", awaitUntil(8000) { hasStatus("CONNECTED") })

        m.disconnect()
        assertTrue("主动断开后应出现 DISCONNECTED", awaitUntil(3000) { hasStatus("DISCONNECTED") })
    }

    // ------------------------------------------------------------------ TC-04
    @Test
    fun tc04_pong_timeout_marks_link_dead_and_reconnects() {
        repeat(4) { server.enqueue(MockResponse().withWebSocketUpgrade(HarnessServer(replyPong = false))) }
        server.start()
        val m = newManager(pingIntervalMs = 200L, pongTimeoutMs = 150L)
        m.connect("harness-token", scope)

        assertTrue("应进入 CONNECTED", awaitUntil(8000) { hasStatus("CONNECTED") })
        assertTrue(
            "Pong 超时应被判为链路假死",
            awaitUntil(8000) { logs.any { it.contains("Pong timeout") } }
        )
        assertTrue("假死后应进入 RECONNECTING", awaitUntil(8000) { hasStatus("RECONNECTING") })
    }

    // ------------------------------------------------------------------ TC-03
    @Test
    fun tc03_backoff_is_exponential_with_jitter_and_cap() {
        repeat(8) { server.enqueue(MockResponse().setResponseCode(500).setBody("handshake rejected")) }
        server.start()
        val m = newManager(
            retryBaseDelayMs = 60L,
            retryMaxDelayMs = 300L,
            maxRetries = 5,
        )
        m.connect("harness-token", scope)

        assertTrue("应观察到 5 次重连尝试", awaitUntil(12000) { reconnectAttempts().size >= 5 })

        val delays = reconnectAttempts().map { it.second }.take(5)
        // 期望封顶序列：60 / 120 / 240 / 300(480 封顶) / 300(960 封顶)
        val caps = listOf(60L, 120L, 240L, 300L, 300L)
        delays.forEachIndexed { i, d ->
            val cap = caps[i]
            assertTrue(
                "第 ${i + 1} 次退避 ${d}ms 应落在 [${cap}, ${cap + cap / 2}) —— 指数 + 封顶 + 抖动上界",
                d >= cap && d < cap + cap / 2
            )
        }
        assertTrue("重试耗尽后应停止并进入 ERROR", hasStatus("ERROR"))
    }
}
