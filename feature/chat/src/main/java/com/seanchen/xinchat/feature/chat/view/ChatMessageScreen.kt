package com.seanchen.xinchat.feature.chat.view

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState
import com.seanchen.xinchat.core.common.base.state.LoadMoreState
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.appbar.CenterTopAppBar
import com.seanchen.xinchat.feature.chat.R
import com.seanchen.xinchat.feature.chat.viewmodel.ChatMessageViewModel

@Composable
internal fun ChatMessageRoute(
    viewModel: ChatMessageViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isLoadingHistory by viewModel.isLoadingHistory.collectAsState()
    val loadMoreState by viewModel.loadMoreState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val newMessageIds by viewModel.newMessageIds.collectAsState()

    ChatMessageScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatMessageScreen(
    uiState: BaseNetWorkUiState<Unit> = BaseNetWorkUiState.Loading,
    messages: List<Msg> = emptyList(),
    isLoadingHistory: Boolean = false,
    loadMoreState: LoadMoreState = LoadMoreState.Success,
    inputText: String = "",
    newMessageIds: Set<Long> = emptySet()
){
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = R.string.messages_title,
                onBackClick = { navigateBack() }
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        BaseNetworkView()
    }

    ChatMessageContentView()
}

@Composable
private fun ChatMessageContentView(){

}