package com.seanchen.xinchat.core.common.base.state

sealed class LoadMoreState {
    object PullToLoad: LoadMoreState()

    object Loading: LoadMoreState()

    object Success: LoadMoreState()

    object Error: LoadMoreState()

    object NoMore: LoadMoreState()
}