package com.seanchen.xinchat.feature.chat.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.widget.ui.appbar.CenterTopAppBar
import com.seanchen.widget.ui.dialog.AppDialog
import com.seanchen.widget.ui.list.AppListItem
import com.seanchen.widget.ui.text.AppText
import com.seanchen.widget.ui.text.TextSize
import com.seanchen.widget.ui.theme.ShapeLarge
import com.seanchen.widget.ui.theme.SpacePaddingLarge
import com.seanchen.widget.ui.theme.SpacePaddingMedium
import com.seanchen.widget.ui.theme.SpaceVerticalMedium
import com.seanchen.widget.ui.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.designsystem.component.AppAvatar
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.util.media.toFullMediaUrl
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.feature.chat.R
import com.seanchen.xinchat.feature.chat.state.ChatInfoUiState
import com.seanchen.xinchat.feature.chat.viewmodel.ChatInfoViewModel

/**
 * 聊天信息界面，点击聊天界面AppBar的菜单按钮进入
 */
@Composable
fun ChatInfoRoute(
    sessionId: Long = 0L,
    viewModel: ChatInfoViewModel = hiltViewModel()
) {
    LaunchedEffect(sessionId) {
        viewModel.init(sessionId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChatInfoScreen(
        uiState = uiState,
        onBackClick = { navigateBack() },
        onTogglePin = viewModel::togglePin,
        onToggleMute = viewModel::toggleMute,
        onClearHistory = viewModel::clearChatHistory
    )
}

@Composable
fun ChatInfoScreen(
    uiState: ChatInfoUiState = ChatInfoUiState(),
    onBackClick: () -> Unit = {},
    onTogglePin: (Boolean) -> Unit = {},
    onToggleMute: (Boolean) -> Unit = {},
    onClearHistory: () -> Unit = {}
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = R.string.chat_info_title,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        ChatInfoContentView(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onTogglePin = onTogglePin,
            onToggleMute = onToggleMute,
            onOpenClearDialog = { showClearConfirmDialog = true }
        )
    }

    if (showClearConfirmDialog) {
        AppDialog(
            title = stringResource(R.string.chat_info_clear_history_title),
            content = stringResource(R.string.chat_info_clear_history_message),
            okText = stringResource(R.string.chat_info_clear_confirm),
            okColor = MaterialTheme.colorScheme.error,
            cancelText = stringResource(R.string.cancel),
            onOk = {
                onClearHistory()
                showClearConfirmDialog = false
                ToastUtils.showSuccess(R.string.chat_info_clear_history_success)
            },
            onCancel = { showClearConfirmDialog = false },
            onDismiss = { showClearConfirmDialog = false }
        )
    }
}

@Composable
fun ChatInfoContentView(
    modifier: Modifier = Modifier,
    uiState: ChatInfoUiState,
    onTogglePin: (Boolean) -> Unit,
    onToggleMute: (Boolean) -> Unit,
    onOpenClearDialog: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = SpacePaddingMedium, vertical = SpaceVerticalMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium)
    ) {
        // 会话成员/对方信息卡片
        Card(
            shape = ShapeLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = SpacePaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceVerticalSmall)
            ) {
                AppAvatar(
                    avatarUrl = uiState.avatarUrl.toFullMediaUrl(),
                    size = 64.dp
                )
                AppText(
                    text = uiState.name.ifBlank { stringResource(R.string.chat_unknown_conversation) },
                    size = TextSize.TITLE_MEDIUM,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 功能设置卡片：置顶聊天、消息免打扰、删除聊天记录
        Card(
            shape = ShapeLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 置顶聊天
                AppListItem(
                    title = stringResource(R.string.chat_info_pin_chat),
                    showArrow = false,
                    showDivider = true,
                    trailingContent = {
                        Switch(
                            checked = uiState.isPinned,
                            onCheckedChange = onTogglePin
                        )
                    },
                    onClick = { onTogglePin(!uiState.isPinned) }
                )

                // 消息免打扰
                AppListItem(
                    title = stringResource(R.string.chat_info_mute_notifications),
                    showArrow = false,
                    showDivider = true,
                    trailingContent = {
                        Switch(
                            checked = uiState.isMuted,
                            onCheckedChange = onToggleMute
                        )
                    },
                    onClick = { onToggleMute(!uiState.isMuted) }
                )

                // 删除聊天记录
                AppListItem(
                    title = stringResource(R.string.chat_info_clear_history),
                    showArrow = true,
                    showDivider = false,
                    onClick = onOpenClearDialog
                )
            }
        }
    }
}