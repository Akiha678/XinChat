package com.seanchen.xinchat.core.network.interceptor

import com.seanchen.xinchat.core.result.SessionExpiryNotifier
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 登录态失效拦截器
 *
 * 认证模块以外的接口（如 contact）在登录态失效时直接返回 HTTP 401，
 * 这里统一上报给 [SessionExpiryNotifier]；登录、注册接口的 401 表示账号或密码错误，
 * 不属于登录态失效，按路径排除。
 */
@Singleton
class SessionExpiryInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val path = response.request.url.encodedPath
        if (response.code == SessionExpiryNotifier.UNAUTHORIZED_CODE && !path.isAuthEntry()) {
            SessionExpiryNotifier.notifyExpired()
        }

        return response
    }

    /**
     * 判断是否为登录、注册入口，这些接口的 401 只表示凭证错误
     */
    private fun String.isAuthEntry(): Boolean {
        return startsWith(AUTH_LOGIN_PREFIX) || startsWith(AUTH_REGISTER_PREFIX)
    }

    private companion object {
        const val AUTH_LOGIN_PREFIX = "/auth/login"
        const val AUTH_REGISTER_PREFIX = "/auth/register"
    }
}
