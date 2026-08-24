package com.seanchen.xinchat.core.model.response

import kotlinx.serialization.Serializable

@Serializable
data class NetworkPageData<T>(
    var list: List<T>? = null,

    var pagination: NetworkPageMeta? = null
)