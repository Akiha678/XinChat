package com.seanchen.xinchat.feature.chat.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState
import com.seanchen.xinchat.core.designsystem.theme.Primary
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingLarge
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.designsystem.theme.appTextColors
import com.seanchen.xinchat.core.navigation.chat.ChatNavigator
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.appbar.CenterTopAppBar
import com.seanchen.xinchat.core.ui.component.empty.Empty
import com.seanchen.xinchat.core.ui.component.network.BaseNetworkView
import com.seanchen.xinchat.feature.chat.R
import com.seanchen.xinchat.feature.chat.state.ChatListUiState
import com.seanchen.xinchat.feature.chat.state.ChatSessionItemUiState
import com.seanchen.xinchat.feature.chat.viewmodel.ChatListViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ChatListRoute(
    showBackIcon: Boolean = false,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    // 页面重新进入组合（冷启动、从聊天详情返回、切回本 Tab）时恢复实时连接并补齐会话摘要
    LaunchedEffect(Unit) {
        viewModel.onScreenVisible()
    }
    // 页面离开组合（被聊天详情页覆盖或切走）时断开本页连接，避免同一账号建立多条连接
    DisposableEffect(Unit) {
        onDispose { viewModel.onScreenHidden() }
    }
    // 应用退到后台时断开连接，回到前台时恢复连接并补齐摘要
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onScreenVisible()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.onScreenHidden()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChatListScreen(
        uiState = uiState,
        showBackIcon = showBackIcon,
        onBackClick = { navigateBack() },
        onRefresh = viewModel::refreshSessions,
        onSessionClick = { session -> ChatNavigator.toChatMessage(session.id) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatListScreen(
    uiState: BaseNetWorkUiState<ChatListUiState> = BaseNetWorkUiState.Loading,
    showBackIcon: Boolean = false,
    onBackClick: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onSessionClick: (ChatSessionItemUiState) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = R.string.messages_title,
                showBackIcon = showBackIcon,
                onBackClick = onBackClick
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars),
        modifier = Modifier.fillMaxSize()
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
        ) { state ->
            ChatListContentView(
                state = state,
                onSessionClick = onSessionClick
            )
        }
    }
}

@Composable
private fun ChatListContentView(
    state: ChatListUiState,
    onSessionClick: (ChatSessionItemUiState) -> Unit,
) {
    if (state.sessions.isEmpty()) {
        Empty(
            message = R.string.messages_empty_title,
            subtitle = R.string.messages_empty_description,
            icon = com.seanchen.xinchat.core.ui.R.drawable.ic_empty_data
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(vertical = SpaceVerticalSmall)
    ) {
        items(
            items = state.sessions,
            key = { it.id }
        ) { session ->
            ChatSessionItem(
                session = session,
                onClick = { onSessionClick(session) }
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 76.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun ChatSessionItem(
    session: ChatSessionItemUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = session.name.ifBlank { stringResource(R.string.chat_unknown_conversation) }
    val description = session.preview.ifBlank { stringResource(R.string.chat_no_messages) }
    val timeText = formatConversationTime(session.lastMessageAt)
    val avatarColor = Color.hsv(
        hue = Math.floorMod(session.colorSeed, 360).toFloat(),
        saturation = 0.35f,
        value = 0.85f
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = SpacePaddingLarge, vertical = SpacePaddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(avatarColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.take(1),
                color = Primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = SpacePaddingMedium),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = appTextColors().primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = appTextColors().tertiary,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = description,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = appTextColors().tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (session.unreadCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = session.unreadCount.coerceAtMost(99).toString(),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(1.dp))
                }
            }
        }
    }
}

private fun formatConversationTime(value: String?): String {
    if (value.isNullOrBlank()) return ""

    return runCatching {
        val dateTime = Instant.parse(value).atZone(ZoneId.systemDefault())
        val today = java.time.LocalDate.now(ZoneId.systemDefault())
        if (dateTime.toLocalDate() == today) {
            DateTimeFormatter.ofPattern("HH:mm").format(dateTime)
        } else {
            DateTimeFormatter.ofPattern("MM/dd").format(dateTime)
        }
    }.getOrDefault(value)
}
