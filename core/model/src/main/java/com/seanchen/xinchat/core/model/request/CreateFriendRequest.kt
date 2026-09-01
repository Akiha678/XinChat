package com.seanchen.xinchat.core.model.request

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class CreateFriendRequest(
    val addresseeId: Long = 0,
    @EncodeDefault
    val message: String = "",
)
