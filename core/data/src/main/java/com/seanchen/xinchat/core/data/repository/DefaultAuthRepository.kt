package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.data.network.LoginRequestDto
import com.seanchen.xinchat.core.data.network.RegisterRequestDto
import com.seanchen.xinchat.core.data.network.XinChatApi
import com.seanchen.xinchat.core.data.network.apiCall
import com.seanchen.xinchat.core.data.network.toModel
import com.seanchen.xinchat.core.data.session.SessionStore
import javax.inject.Inject
import kotlinx.serialization.json.Json

internal class DefaultAuthRepository @Inject constructor(
    private val api: XinChatApi,
    private val sessionStore: SessionStore,
    private val json: Json,
) : AuthRepository {
    override val session = sessionStore.session

    override suspend fun login(username: String, password: String) {
        val session = apiCall(json) {
            api.login(LoginRequestDto(username.trim(), password))
        }.toModel()
        sessionStore.save(session)
    }

    override suspend fun register(
        username: String,
        displayName: String,
        email: String,
        password: String,
    ) {
        val session = apiCall(json) {
            api.register(
                RegisterRequestDto(
                    username = username.trim(),
                    displayName = displayName.trim(),
                    email = email.trim(),
                    password = password,
                ),
            )
        }.toModel()
        sessionStore.save(session)
    }

    override suspend fun logout() = sessionStore.clear()
}
