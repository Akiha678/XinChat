package com.seanchen.xinchat.core.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserSummaryResponse(
    val id: Long = 0,
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val avatarColor: Int = 0,
)
