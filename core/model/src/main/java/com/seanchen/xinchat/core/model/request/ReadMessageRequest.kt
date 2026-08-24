package com.seanchen.xinchat.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ReadMessageRequest (
    val ids: List<Long>
)