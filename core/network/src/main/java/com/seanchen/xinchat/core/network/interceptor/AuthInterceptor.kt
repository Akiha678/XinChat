package com.seanchen.xinchat.core.network.interceptor

import com.seanchen.xinchat.core.datastore.datasource.auth.AuthStoreDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authStoreDataSource: AuthStoreDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking {
            authStoreDataSource.getToken() ?: ""
        }

        val request = if (token.isNotBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", token)
                .build()
        } else {
            originalRequest
        }
        return chain.proceed(request)
    }
}