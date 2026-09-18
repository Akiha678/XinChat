package com.seanchen.xinchat.core.result

import com.seanchen.xinchat.core.model.response.NetworkResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * 业务码 401 上报测试
 */
class SessionExpiryNotifierTest {

    @Test
    fun `handleResultWithData reports expiry when business code is unauthorized`() = runBlocking {
        val expired = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeoutOrNull(EVENT_TIMEOUT_MILLIS) { SessionExpiryNotifier.events.first() }
        }

        ResultHandler.handleResultWithData(
            scope = this,
            flow = flowOf(
                NetworkResponse<Any>(
                    code = SessionExpiryNotifier.UNAUTHORIZED_CODE,
                    message = "登录状态无效或已过期"
                )
            ).asResult(),
            showToast = false,
            onData = {}
        )

        assertNotNull("业务码 401 未上报登录态失效", expired.await())
    }

    @Test
    fun `handleResultWithData returns data when business code succeeds`() = runBlocking {
        val received = CompletableDeferred<Any>()

        ResultHandler.handleResultWithData(
            scope = this,
            flow = flowOf(NetworkResponse<Any>(code = SUCCESS_CODE, data = "ok")).asResult(),
            showToast = false,
            onData = { received.complete(it) }
        )

        assertEquals("ok", withTimeout(EVENT_TIMEOUT_MILLIS) { received.await() })
    }

    private companion object {
        const val EVENT_TIMEOUT_MILLIS = 3_000L
        const val SUCCESS_CODE = 1000
    }
}
