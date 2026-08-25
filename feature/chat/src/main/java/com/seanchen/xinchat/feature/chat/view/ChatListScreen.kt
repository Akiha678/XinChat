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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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

@Composable
fun ChatListRoute(
    showBackIcon: Boolean = false,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChatListScreen(
        uiState = uiState,
        showBackIcon = showBackIcon,
        onBackClick = { navigateBack() },
        onRefresh = viewModel::refreshSessions,
        onSessionClick = { ChatNavigator.toChatMessage() }
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
    val title = stringResource(R.string.chat_online_support)
    val description = stringResource(R.string.chat_online_support_description)
    val timeText = stringResource(R.string.chat_time_now)

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
                .background(Primary.copy(alpha = 0.12f)),
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