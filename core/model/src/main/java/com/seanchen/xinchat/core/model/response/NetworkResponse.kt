package com.seanchen.xinchat.core.model.response

import kotlinx.serialization.Serializable

@Serializable
data class NetworkResponse<T>(

    val data: T? = null,

    val code: Int = 1000,


    val message: String? = null,
) {
    val isSucceeded : Boolean get() = code == 1000
}