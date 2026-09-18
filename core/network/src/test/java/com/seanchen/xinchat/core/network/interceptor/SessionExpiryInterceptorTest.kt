package com.seanchen.xinchat.core.network.interceptor

import com.seanchen.xinchat.core.result.SessionExpiryNotifier
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * HTTP 401 上报登录态失效测试
 */
class SessionExpiryInterceptorTest {

    private val interceptor = SessionExpiryInterceptor()

    @Test
    fun `http unauthorized reports session expiry`() = runBlocking {
        val expired = observeExpiry()

        interceptor.intercept(chainWith(code = UNAUTHORIZED_CODE, path = "/contact/friends"))

        assertNotNull("HTTP 401 未上报登录态失效", expired.await())
    }

    @Test
    fun `login failure does not report session expiry`() = runBlocking {
        val expired = observeExpiry(timeoutMillis = NEGATIVE_TIMEOUT_MILLIS)

        interceptor.intercept(chainWith(code = UNAUTHORIZED_CODE, path = "/auth/login/password"))

        assertNull("登录凭证错误不应上报登录态失效", expired.await())
    }

    @Test
    fun `successful response does not report session expiry`() = runBlocking {
        val expired = observeExpiry(timeoutMillis = NEGATIVE_TIMEOUT_MILLIS)

        interceptor.intercept(chainWith(code = SUCCESS_CODE, path = "/contact/friends"))

        assertNull("正常响应不应上报登录态失效", expired.await())
    }

    /**
     * 先订阅失效事件，避免断言早于事件到达
     */
    private fun CoroutineScope.observeExpiry(
        timeoutMillis: Long = POSITIVE_TIMEOUT_MILLIS
    ): Deferred<Unit?> = async(start = CoroutineStart.UNDISPATCHED) {
        withTimeoutOrNull(timeoutMillis) { SessionExpiryNotifier.events.first() }
    }

    private fun chainWith(code: Int, path: String): Interceptor.Chain {
        val request = Request.Builder()
            .url("http://127.0.0.1:8080$path")
            .build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("stub")
            .build()
        return FakeChain(response)
    }

    private companion object {
        const val UNAUTHORIZED_CODE = 401
        const val SUCCESS_CODE = 200
        const val POSITIVE_TIMEOUT_MILLIS = 3_000L
        const val NEGATIVE_TIMEOUT_MILLIS = 300L
    }
}

/**
 * 只提供拦截器实际使用的成员，其余调用直接失败
 */
private class FakeChain(private val response: Response) : Interceptor.Chain {
    override fun request(): Request = response.request

    override fun proceed(request: Request): Response = response

    override fun connection(): Connection? = null

    override fun call(): Call = throw UnsupportedOperationException("测试桩不支持 call()")

    override fun connectTimeoutMillis(): Int = TIMEOUT_MILLIS

    override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun readTimeoutMillis(): Int = TIMEOUT_MILLIS

    override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    override fun writeTimeoutMillis(): Int = TIMEOUT_MILLIS

    override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this

    private companion object {
        const val TIMEOUT_MILLIS = 10_000
    }
}
