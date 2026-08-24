package com.seanchen.xinchat.core.ui.component.empty

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seanchen.xinchat.core.ui.R

@Composable
fun EmptyNetwork(
    modifier: Modifier = Modifier,
    onRetryClick: (() -> Unit)? = null
) {
    Empty(
        modifier = modifier,
        message = R.string.empty_network,
        subtitle = R.string.empty_network_subtitle,
        icon = R.drawable.ic_empty_network,
        retryButtonText = R.string.click_retry,
        onRetryClick = onRetryClick
    )
}