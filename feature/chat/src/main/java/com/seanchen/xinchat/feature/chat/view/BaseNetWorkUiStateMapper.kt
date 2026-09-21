package com.seanchen.xinchat.feature.chat.view

import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState as CoreNetWorkState
import com.seanchen.widget.ui.error.BaseNetWorkUiState as WidgetNetWorkState

/**
 * 将 core:common 的网络状态映射为组件库（com.seanchen.widget.ui）的状态。
 *
 * 组件库的 BaseNetworkView 只接受组件库自己的状态类型，而 ViewModel 侧沿用的是 core:common
 * 的类型（避免业务/数据层依赖 UI 组件库）。两个 sealed class 结构一致，此映射为纯类型转换，
 * 成功态数据原样透传。
 */
internal fun <T> CoreNetWorkState<T>.toWidgetState(): WidgetNetWorkState<T> = when (this) {
    is CoreNetWorkState.Loading -> WidgetNetWorkState.Loading
    is CoreNetWorkState.Success -> WidgetNetWorkState.Success(data)
    is CoreNetWorkState.Error -> WidgetNetWorkState.Error(message, exception)
}