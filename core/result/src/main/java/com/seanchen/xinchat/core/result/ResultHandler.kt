package com.seanchen.xinchat.core.result

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * 网络请求结果处理工具类
 */
object ResultHandler {
    fun <T> handleResult(
        scope: CoroutineScope,
        flow: Flow<Result<NetworkResponse<T>>>,
        showToast: Boolean = true,
        onLoading: () -> Unit = {},
        onSuccess: (NetworkResponse<T>) -> Unit = {},
    ) {

    }
}