package com.seanchen.xinchat.core.model.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val id: Long = 0,
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val avatarColor: Int = 0,
    val accessToken: String = "",
    val expiresAt: String = ""
)
