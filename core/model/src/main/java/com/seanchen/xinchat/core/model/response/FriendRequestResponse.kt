package com.seanchen.xinchat.core.model.response

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestResponse(
    val id: Long = 0,
    val requester: UserSummaryResponse? = null,
    val addressee: UserSummaryResponse? = null,
    val status: String = "",
    val message: String? = null,
    val createdAt: String? = null,
)
