package com.seanchen.xinchat.feature.chat.view

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState
import com.seanchen.xinchat.core.common.base.state.LoadMoreState
import com.seanchen.xinchat.core.designsystem.theme.ShapeExtraLarge
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.appbar.CenterTopAppBar
import com.seanchen.xinchat.core.ui.component.empty.Empty
import com.seanchen.xinchat.core.ui.component.loading.WeLoadingMP
import com.seanchen.xinchat.core.ui.component.network.BaseNetworkView
import com.seanchen.xinchat.feature.chat.R
import com.seanchen.xinchat.feature.chat.component.Message
import com.seanchen.xinchat.feature.chat.viewmodel.ChatMessageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun ChatMessageRoute(
    viewModel: ChatMessageViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isLoadingHistory by viewModel.isLoadingHistory.collectAsStateWithLifecycle()
    val loadMoreState by viewModel.loadMoreState.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val newMessageIds by viewModel.newMessageIds.collectAsStateWithLifecycle()

    ChatMessageScreen(
        uiState = uiState,
        messages = messages,
        isLoadingHistory = isLoadingHistory,
        loadMoreState = loadMoreState,
        inputText = inputText,
        newMessageIds = newMessageIds,
        onRefresh = viewModel::retryRequest,
        onBackClick = { navigateBack() },
        onLoadMore = viewModel::loadMoreMessages,
        onSendMessage = viewModel::sendMessage,
        onInputTextChange = viewModel::updateInputText,
        onMarkAsRead = viewModel::markMessagesAsRead,
        newMessageEvent = viewModel.newMessageEvent,
        onClearMessageAnimation = viewModel::clearMessageAnimation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatMessageScreen(
    uiState: BaseNetWorkUiState<Unit> = BaseNetWorkUiState.Loading,
    messages: List<Msg> = emptyList(),
    isLoadingHistory: Boolean = false,
    loadMoreState: LoadMoreState = LoadMoreState.Success,
    inputText: String = "",
    newMessageIds: Set<Long> = emptySet(),
    onRefresh: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onSendMessage: () -> Unit = {},
    onInputTextChange: (String) -> Unit = {},
    onMarkAsRead: () -> Unit = {},
    newMessageEvent: Flow<Unit>? = null,
    onClearMessageAnimation: (Long) -> Unit = {}
){
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = R.string.messages_title,
                onBackClick = onBackClick
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
        BaseNetworkView(
            uiState = uiState,
            modifier = Modifier.fillMaxSize(),
            padding = paddingValues,
            onRetry = onRefresh,
            chatError = {
                Empty(
                    message = R.string.load_messages_failed,
                    retryButtonText = R.string.retry,
                    onRetryClick = onRefresh
                )
            }
        ) {
            ChatMessageContentView(
                messages = messages,
                isLoadingHistory = isLoadingHistory,
                loadMoreState = loadMoreState,
                inputText = inputText,
                newMessageIds = newMessageIds,
                onLoadMore = onLoadMore,
                onSendMessage = onSendMessage,
                onInputTextChange = onInputTextChange,
                onClearMessageAnimation = onClearMessageAnimation,
                onMarkAsRead = onMarkAsRead,
                newMessageEvent = newMessageEvent
            )
        }
    }
}

@Composable
private fun ChatMessageContentView(
    modifier: Modifier = Modifier,
    messages: List<Msg>,
    isLoadingHistory: Boolean,
    loadMoreState: LoadMoreState,
    inputText: String,
    newMessageIds: Set<Long>,
    onLoadMore: () -> Unit,
    onSendMessage: () -> Unit,
    onInputTextChange: (String) -> Unit,
    onClearMessageAnimation: (Long) -> Unit,
    onMarkAsRead: () -> Unit,
    newMessageEvent: Flow<Unit>? = null
){
    val scrollState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.any { it.status == 0 }) {
            onMarkAsRead()
        }
    }

    LaunchedEffect(newMessageEvent) {
        newMessageEvent?.collect {
            delay(50)

            if (scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0) {
                scrollState.scrollToItem(0, 100)
                delay(16)
            }
            scrollState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(messages.size, loadMoreState) {
        snapshotFlow {
            scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            val shouldLoadMore = lastVisibleIndex != null &&
                    messages.isNotEmpty() &&
                    lastVisibleIndex >= messages.lastIndex - 1

            if (shouldLoadMore) {
                onLoadMore()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
    ) {
        if (messages.isEmpty()) {
            Empty(
                modifier = Modifier.weight(1f),
                message = R.string.messages_empty_title,
                subtitle = R.string.messages_empty_description,
                icon = com.seanchen.xinchat.core.ui.R.drawable.ic_empty_data
            )
        } else {
            LazyColumn(
                state = scrollState,
                reverseLayout = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    top = SpaceVerticalMedium,
                    bottom = SpaceVerticalMedium
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(
                    items = messages,
                    key = { _, message -> message.id }
                ) { index, message ->
                    val visualPrevious = messages.getOrNull(index + 1)
                    val visualNext = messages.getOrNull(index - 1)
                    Message(
                        msg = message,
                        isUserMe = message.type == 0,
                        isFirstMessageByAuthor = visualPrevious?.userId != message.userId,
                        isLastMessageByAuthor = visualNext?.userId != message.userId,
                        isNewMessage = message.id in newMessageIds,
                        onAnimationFinished = { onClearMessageAnimation(message.id) }
                    )
                }

                item(key = "load_more") {
                    LoadMoreFooter(
                        loadMoreState = loadMoreState,
                        isLoadingHistory = isLoadingHistory,
                        onLoadMore = onLoadMore
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ChatInputBar(
            inputText = inputText,
            onInputTextChange = onInputTextChange,
            onSendMessage = onSendMessage
        )
    }
}

@Composable
private fun LoadMoreFooter(
    loadMoreState: LoadMoreState,
    isLoadingHistory: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val text = when (loadMoreState) {
        LoadMoreState.Loading -> R.string.chat_loading_history
        LoadMoreState.Error -> R.string.chat_load_history_failed
        LoadMoreState.NoMore -> R.string.chat_no_more_history
        else -> R.string.chat_load_history
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpaceVerticalSmall),
        contentAlignment = Alignment.Center
    ) {
        if (loadMoreState == LoadMoreState.Loading || isLoadingHistory) {
            WeLoadingMP()
        } else {
            TextButton(
                enabled = loadMoreState != LoadMoreState.NoMore,
                onClick = onLoadMore
            ) {
                Text(
                    text = stringResource(text),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val canSend = inputText.isNotBlank()

    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacePaddingMedium),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(SpacePaddingMedium)
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputTextChange,
            modifier = Modifier
                .weight(1f)
                .widthIn(min = 0.dp),
            placeholder = {
                Text(text = stringResource(R.string.message_input_hint))
            },
            shape = ShapeExtraLarge,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (canSend) {
                        onSendMessage()
                        focusManager.clearFocus()
                    }
                }
            )
        )

        TextButton(
            enabled = canSend,
            onClick = {
                onSendMessage()
                focusManager.clearFocus()
            },
            modifier = Modifier.size(width = 64.dp, height = 56.dp)
        ) {
            Text(text = stringResource(R.string.send))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatMessageScreenPreview() {
    MaterialTheme {
        ChatMessageScreen(
            uiState = BaseNetWorkUiState.Success(Unit),
            messages = listOf(
                Msg(
                    id = 2,
                    userId = 100,
                    nickName = "客服",
                    content = Msg.MessageContent(type = "text", data = "您好，有什么可以帮您？"),
                    type = 1
                ),
                Msg(
                    id = 1,
                    userId = 1,
                    nickName = "我",
                    content = Msg.MessageContent(type = "text", data = "我想了解订单状态。"),
                    type = 0
                )
            ),
            inputText = "谢谢"
        )
    }
}
