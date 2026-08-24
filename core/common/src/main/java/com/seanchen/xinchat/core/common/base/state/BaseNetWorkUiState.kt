package com.seanchen.xinchat.core.common.base.state

sealed class BaseNetWorkUiState<out T> {
    /**
     * 加载中状态
     */
    data object Loading : BaseNetWorkUiState<Nothing>()

    /**
     * 成功状态
     */
    data class Success<T>(var data: T) : BaseNetWorkUiState<T>()

    /**
     * 错误状态
     */
    data class Error(val message: String? = null, val exception: Throwable? = null) : BaseNetWorkUiState<Nothing>()
}