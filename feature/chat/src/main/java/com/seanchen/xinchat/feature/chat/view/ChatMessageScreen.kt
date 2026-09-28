package com.seanchen.xinchat.feature.chat.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import com.seanchen.xinchat.core.designsystem.theme.ShapeMedium
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingMedium
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState
import com.seanchen.xinchat.core.common.base.state.LoadMoreState
import com.seanchen.xinchat.core.designsystem.component.AppColumn
import com.seanchen.xinchat.core.designsystem.component.FullScreenBox
import com.seanchen.xinchat.core.designsystem.theme.ShapeExtraLarge
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingSmall
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.widget.ui.empty.Empty
import com.seanchen.widget.ui.loading.WeLoadingMP
import com.seanchen.widget.ui.tag.Tag
import com.seanchen.widget.ui.tag.TagStyle
import com.seanchen.widget.ui.tag.TagType
import com.seanchen.xinchat.feature.chat.R
import com.seanchen.xinchat.feature.chat.component.ChatInputArea
import com.seanchen.xinchat.feature.chat.component.Message
import com.seanchen.xinchat.feature.chat.viewmodel.ChatMessageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.stringResource
import com.seanchen.xinchat.core.navigation.chat.ChatNavigator
import com.seanchen.xinchat.core.navigation.contact.ContactNavigator
import com.seanchen.widget.ui.appbar.MenuAppBar

@Composable
internal fun ChatMessageRoute(
    sessionId: Long = 0L,
    viewModel: ChatMessageViewModel = hiltViewModel()
){
    LaunchedEffect(sessionId) {
        viewModel.openSession(sessionId)
    }

    // 应用从后台回到前台时恢复实时连接（连接在后台期间可能已被服务端断开）
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.connectWebSocket()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isLoadingHistory by viewModel.isLoadingHistory.collectAsStateWithLifecycle()
    val loadMoreState by viewModel.loadMoreState.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val newMessageIds by viewModel.newMessageIds.collectAsStateWithLifecycle()
    val currentSessionId by viewModel.sessionId.collectAsStateWithLifecycle()
    val activeSessionId = if (sessionId > 0) sessionId else currentSessionId

    ChatMessageScreen(
        uiState = uiState,
        messages = messages,
        currentUserAvatarUrl = currentUser?.avatarUrl,
        isLoadingHistory = isLoadingHistory,
        loadMoreState = loadMoreState,
        inputText = inputText,
        newMessageIds = newMessageIds,
        onRefresh = viewModel::retryRequest,
        onBackClick = { navigateBack() },
        onMenuClick = {
            if (activeSessionId > 0) {
                ChatNavigator.toChatInfo(activeSessionId)
            }
        },
        onAvatarClick = { userId ->
            ContactNavigator.toFriendInfo(userId = userId)
        },
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
    currentUserAvatarUrl: String? = null,
    isLoadingHistory: Boolean = false,
    loadMoreState: LoadMoreState = LoadMoreState.Success,
    inputText: String = "",
    newMessageIds: Set<Long> = emptySet(),
    onRefresh: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onAvatarClick: (Long) -> Unit = {},
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
            // 组件库使用方案
            MenuAppBar(
                title = R.string.messages_title,
                onBackClick = onBackClick,
                onMenuClick = onMenuClick,
                scrollBehavior = scrollBehavior
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
        ChatMessageContentView(
            modifier = Modifier.padding(paddingValues),
            messages = messages,
            uiState = uiState,
            currentUserAvatarUrl = currentUserAvatarUrl,
            isLoadingHistory = isLoadingHistory,
            loadMoreState = loadMoreState,
            inputText = inputText,
            newMessageIds = newMessageIds,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
            onSendMessage = onSendMessage,
            onInputTextChange = onInputTextChange,
            onClearMessageAnimation = onClearMessageAnimation,
            onMarkAsRead = onMarkAsRead,
            onAvatarClick = onAvatarClick,
            newMessageEvent = newMessageEvent
        )
    }
}

@Composable
private fun ChatMessageContentView(
    modifier: Modifier = Modifier,
    messages: List<Msg>,
    uiState: BaseNetWorkUiState<Unit> = BaseNetWorkUiState.Loading,
    currentUserAvatarUrl: String? = null,
    isLoadingHistory: Boolean,
    loadMoreState: LoadMoreState,
    inputText: String,
    newMessageIds: Set<Long>,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit,
    onSendMessage: () -> Unit,
    onInputTextChange: (String) -> Unit,
    onClearMessageAnimation: (Long) -> Unit,
    onMarkAsRead: () -> Unit,
    onAvatarClick: (Long) -> Unit = {},
    newMessageEvent: Flow<Unit>? = null
){
    val scrollState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.any { it.status == 0 && it.type != 0 }) {
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
            val isScrolling = scrollState.isScrollInProgress
            val lastVisible = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            isScrolling to lastVisible
        }.distinctUntilChanged().collect { (isScrolling, lastVisibleIndex) ->
            val shouldLoadMore = isScrolling &&
                    lastVisibleIndex != null &&
                    messages.isNotEmpty() &&
                    lastVisibleIndex >= messages.lastIndex - 1 &&
                    loadMoreState == LoadMoreState.PullToLoad

            if (shouldLoadMore) {
                onLoadMore()
            }
        }
    }

    FullScreenBox(
        // 键盘弹出时由 IME insets 收缩内容区（配合 adjustResize），
        // 输入区始终贴在键盘上方，不会出现整屏被推起、输入框跑到顶部的问题。
        modifier = modifier.imePadding()
    ) {
        AppColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 消息列表占满剩余空间，保证输入区固定在底部
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val contentState = when {
                    messages.isNotEmpty() -> ChatContentState.Content
                    uiState is BaseNetWorkUiState.Loading -> ChatContentState.Loading
                    uiState is BaseNetWorkUiState.Error -> ChatContentState.Error
                    else -> ChatContentState.Empty
                }

                Crossfade(
                    targetState = contentState,
                    animationSpec = tween(durationMillis = 280),
                    label = "ChatContentCrossfade"
                ) { state ->
                    when (state) {
                        ChatContentState.Loading -> {
                            ChatSkeletonPlaceholder()
                        }
                        ChatContentState.Error -> {
                            Empty(
                                modifier = Modifier.fillMaxSize(),
                                message = R.string.load_messages_failed,
                                retryButtonText = R.string.retry,
                                onRetryClick = onRefresh
                            )
                        }
                        ChatContentState.Empty -> {
                            Empty(
                                modifier = Modifier.fillMaxSize(),
                                message = R.string.messages_empty_title,
                                subtitle = R.string.messages_empty_description,
                            )
                        }
                        ChatContentState.Content -> {
                            LazyColumn(
                                state = scrollState,
                                reverseLayout = true,
                                modifier = Modifier.fillMaxSize(),
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
                                        currentUserAvatarUrl = currentUserAvatarUrl,
                                        onAnimationFinished = { onClearMessageAnimation(message.id) },
                                        onAvatarClick = onAvatarClick
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
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            ChatInputArea(
                inputText = inputText,
                onInputTextChange = onInputTextChange,
                onSendMessage = onSendMessage,
                modifier = Modifier.fillMaxWidth()
            )
        }
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
        LoadMoreState.Loading -> R.string.loading_history
        LoadMoreState.Error -> R.string.load_failed_retry
        LoadMoreState.NoMore -> R.string.no_more_messages
        else -> R.string.load_more_history
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

/**
 * 跳转至底部按钮组件
 */
@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    AnimatedVisibility(
        visible = enabled,
        enter = fadeIn(animationSpec = tween (durationMillis = 300)) +
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
        exit = fadeOut(tween(200)) +
                scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(200)
                ),
        modifier = modifier
    ) {
        Tag(
            text = stringResource(R.string.back_to_bottom),
            shape = ShapeExtraLarge,
            type = TagType.PRIMARY,
            style = TagStyle.LIGHT,
            modifier = Modifier
                .padding(bottom = SpacePaddingSmall)
                .clip(ShapeExtraLarge)
                .clickable(onClick = onClicked)
        )
    }
}

private enum class ChatContentState {
    Loading, Error, Empty, Content
}

/**
 * 带有呼吸微光动效的聊天气泡骨架屏组件
 */
@Composable
private fun ChatSkeletonPlaceholder(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "SkeletonTransition")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.60f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SkeletonAlpha"
    )
    val shimmerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)

    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SpacePaddingMedium, vertical = SpaceVerticalMedium),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 时间戳微弱占位
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 80.dp, height = 14.dp)
                .clip(CircleShape)
                .background(shimmerColor)
        )

        // 对方消息骨架 (左)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(shimmerColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 150.dp, height = 40.dp)
                    .clip(ShapeMedium)
                    .background(shimmerColor)
            )
        }

        // 当前用户消息骨架 (右)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(width = 200.dp, height = 52.dp)
                    .clip(ShapeMedium)
                    .background(shimmerColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                .background(shimmerColor)
            )
        }

        // 对方长消息骨架 (左)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(shimmerColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 230.dp, height = 64.dp)
                    .clip(ShapeMedium)
                    .background(shimmerColor)
            )
        }
    }
}