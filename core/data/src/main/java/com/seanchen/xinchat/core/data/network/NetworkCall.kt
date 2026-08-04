package com.seanchen.xinchat.core.data.network

import com.seanchen.xinchat.core.data.session.SessionStore
import java.io.IOException
import kotlinx.serialization.json.Json
import retrofit2.HttpException

class XinChatDataException(message: String, cause: Throwable? = null) : Exception(message, cause)

internal suspend fun <T> apiCall(
    json: Json,
    sessionStore: SessionStore? = null,
    block: suspend () -> T,
): T = try {
    block()
} catch (exception: HttpException) {
    if (exception.code() == 401) sessionStore?.clear()
    val message = exception.response()?.errorBody()?.string()
        ?.let { body -> runCatching { json.decodeFromString<ApiErrorDto>(body).message }.getOrNull() }
        ?: "请求失败（${exception.code()}）"
    throw XinChatDataException(message, exception)
} catch (exception: IOException) {
    throw XinChatDataException("无法连接服务器，请检查网络和服务地址", exception)
}

internal suspend fun SessionStore.authorizationHeader(): String {
    val token = current()?.accessToken ?: throw XinChatDataException("登录状态已失效，请重新登录")
    return "Bearer $token"
}
