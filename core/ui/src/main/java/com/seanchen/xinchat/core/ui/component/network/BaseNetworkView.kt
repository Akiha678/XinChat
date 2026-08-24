package com.seanchen.xinchat.core.ui.component.network

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState
import com.seanchen.xinchat.core.ui.component.empty.EmptyNetwork
import com.seanchen.xinchat.core.ui.component.loading.PageLoading


@Composable
fun <T> BaseNetworkView(
    uiState: BaseNetWorkUiState<T>,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    onRetry:() -> Unit = {},
    chatLoading: @Composable (() -> Unit)? = null,
    chatError: @Composable (() -> Unit)? = null,
    content: @Composable (data: T) -> Unit
) {
    Box(
        modifier = modifier.padding(padding),
    ) {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "NetworkStateAnimation"
        ) { state ->
            when (state) {
                is BaseNetWorkUiState.Loading -> {
                    if (chatLoading != null) {
                        chatLoading()
                    } else {
                        PageLoading()
                    }
                }

                is BaseNetWorkUiState.Error -> {
                    if (chatError != null) {
                        chatError()
                    } else {
                        EmptyNetwork()
                    }
                }

                is BaseNetWorkUiState.Success -> content(state.data)
            }
        }
    }
}