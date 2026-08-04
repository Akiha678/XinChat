package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.data.model.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<UserSession?>

    suspend fun login(username: String, password: String)

    suspend fun register(
        username: String,
        displayName: String,
        email: String,
        password: String,
    )

    suspend fun logout()
}
