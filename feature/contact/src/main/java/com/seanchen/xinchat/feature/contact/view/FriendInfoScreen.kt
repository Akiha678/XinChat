package com.seanchen.xinchat.feature.contact.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.feature.contact.viewmodel.FriendInfoViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seanchen.widget.ui.appbar.MenuAppBar
import com.seanchen.widget.ui.image.LetterAvatar
import com.seanchen.widget.ui.list.AppListItem
import com.seanchen.widget.ui.theme.ShapeLarge
import com.seanchen.widget.ui.theme.ShapeMedium
import com.seanchen.widget.ui.theme.SpaceHorizontalMedium
import com.seanchen.widget.ui.theme.SpacePaddingLarge
import com.seanchen.widget.ui.theme.SpacePaddingMedium
import com.seanchen.widget.ui.theme.SpaceVerticalMedium
import com.seanchen.widget.ui.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.designsystem.component.AppAvatar
import com.seanchen.xinchat.core.navigation.chat.ChatNavigator
import com.seanchen.xinchat.core.util.media.toFullMediaUrl
import com.seanchen.xinchat.feature.contact.R
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import com.seanchen.xinchat.feature.contact.state.FriendInfoUiState
import com.seanchen.xinchat.feature.contact.state.toFriendInfoUiState

/**
 * 好友资料界面（支持外部导航与路由调用）
 */
@Composable
fun FriendInfoRoute(
    userId: Long = 0L,
    user: ContactUserUiState? = null,
    viewModel: FriendInfoViewModel = hiltViewModel(),
    onBackClick: () -> Unit = { navigateBack() },
    onSendMessage: (Long) -> Unit = { sessionId ->
        ChatNavigator.toChatMessage(sessionId = sessionId)
    },
    onAudioVideoCall: (Long) -> Unit = {}
) {
    LaunchedEffect(userId) {
        if (userId > 0L) {
            viewModel.loadFriendInfo(userId)
        }
    }

    val vmUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiState = remember(user, vmUiState) {
        user?.toFriendInfoUiState() ?: vmUiState
    }

    FriendInfoScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSendMessageClick = {
            viewModel.startChatWithFriend(uiState.id) { sessionId ->
                onSendMessage(sessionId)
            }
        },
        onAudioVideoCallClick = { onAudioVideoCall(uiState.id) }
    )
}

/**
 * 好友资料屏幕（包含顶栏与整体容器）
 * 分成四段，从上到下分别是：
 * 1. AppBar（MenuAppBar）
 * 2. 信息（头像、昵称、XinChat号、地区）
 * 3. 朋友资料（设置备注和标签、朋友权限、个性签名、来源）
 * 4. 发消息和音视频通话（底部双主操作按钮）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendInfoScreen(
    uiState: FriendInfoUiState = FriendInfoUiState(),
    onBackClick: () -> Unit = {},
    onMenuClick: (() -> Unit)? = null,
    onSendMessageClick: () -> Unit = {},
    onAudioVideoCallClick: () -> Unit = {},
    onRemarkClick: () -> Unit = {},
    onPermissionClick: () -> Unit = {},
    onSignatureClick: () -> Unit = {}
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // 第一段：AppBar（MenuAppBar，包含返回按钮与Menu菜单按钮）
            Box {
                MenuAppBar(
                    title = R.string.friend_info_title,
                    onBackClick = onBackClick,
                    onMenuClick = {
                        if (onMenuClick != null) {
                            onMenuClick()
                        } else {
                            isMenuExpanded = true
                        }
                    }
                )
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.friend_info_menu_star)) },
                        onClick = { isMenuExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.friend_info_menu_remark)) },
                        onClick = {
                            isMenuExpanded = false
                            onRemarkClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.friend_info_menu_blacklist)) },
                        onClick = { isMenuExpanded = false }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.friend_info_menu_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = { isMenuExpanded = false }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        FriendInfoContentView(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onSendMessageClick = onSendMessageClick,
            onAudioVideoCallClick = onAudioVideoCallClick,
            onRemarkClick = onRemarkClick,
            onPermissionClick = onPermissionClick,
            onSignatureClick = onSignatureClick
        )
    }
}

/**
 * 好友资料内容组件（从上到下按四段规划，除去顶部AppBar后包含：信息、朋友资料、发消息与音视频通话）
 */
@Composable
fun FriendInfoContentView(
    modifier: Modifier = Modifier,
    uiState: FriendInfoUiState = FriendInfoUiState(),
    onSendMessageClick: () -> Unit = {},
    onAudioVideoCallClick: () -> Unit = {},
    onRemarkClick: () -> Unit = {},
    onPermissionClick: () -> Unit = {},
    onSignatureClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = SpacePaddingMedium, vertical = SpaceVerticalMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium)
    ) {
        // 第二段：信息（头像、昵称、微信号/XinChat号、地区）
        FriendProfileHeaderSection(uiState = uiState)

        // 第三段：朋友资料（设置备注和标签、朋友权限、个性签名、来源等）
        FriendDetailsSection(
            uiState = uiState,
            onRemarkClick = onRemarkClick,
            onPermissionClick = onPermissionClick,
            onSignatureClick = onSignatureClick
        )

        // 第四段：发消息和音视频通话操作按钮
        FriendActionButtonsSection(
            onSendMessageClick = onSendMessageClick,
            onAudioVideoCallClick = onAudioVideoCallClick
        )
    }
}

/**
 * 第二段：信息展示区
 */
@Composable
private fun FriendProfileHeaderSection(
    uiState: FriendInfoUiState
) {
    Card(
        shape = ShapeLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SpacePaddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier.size(64.dp)
            ) {
                val fullUrl = uiState.avatarUrl.toFullMediaUrl()
                if (fullUrl != null) {
                    AppAvatar(
                        avatarUrl = fullUrl,
                        size = 64.dp,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    LetterAvatar(
                        name = uiState.displayName,
                        size = 64.dp,
                        modifier = Modifier.matchParentSize(),
                        backgroundColor = if (uiState.avatarColor != 0) Color(uiState.avatarColor) else null,
                        shape = RoundedCornerShape(12.dp),
                        isOnline = false
                    )
                }
                if (uiState.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color(0xFF00C853), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(SpaceHorizontalMedium))

            // 文本信息列
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 昵称
                Text(
                    text = uiState.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // XinChat号 / 微信号
                if (uiState.username.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.friend_info_username, uiState.username),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 地区
                Text(
                    text = stringResource(R.string.friend_info_region) + "：" + uiState.region,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 第三段：朋友资料展示区
 */
@Composable
private fun FriendDetailsSection(
    uiState: FriendInfoUiState,
    onRemarkClick: () -> Unit,
    onPermissionClick: () -> Unit,
    onSignatureClick: () -> Unit
) {
    Card(
        shape = ShapeLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 设置备注和标签
            AppListItem(
                title = stringResource(R.string.friend_info_remark_and_tag),
                trailingText = if (uiState.remark.isNotBlank()) uiState.remark else null,
                showArrow = true,
                showDivider = true,
                onClick = onRemarkClick
            )

            // 朋友权限
            AppListItem(
                title = stringResource(R.string.friend_info_permission),
                showArrow = true,
                showDivider = true,
                onClick = onPermissionClick
            )

            // 个性签名
            AppListItem(
                title = stringResource(R.string.friend_info_signature),
                trailingText = if (uiState.signature.isNotBlank()) {
                    uiState.signature
                } else {
                    stringResource(R.string.friend_info_signature_empty)
                },
                showArrow = true,
                showDivider = true,
                onClick = onSignatureClick
            )

            // 来源
            AppListItem(
                title = stringResource(R.string.friend_info_source),
                trailingText = uiState.source,
                showArrow = false,
                showDivider = false
            )
        }
    }
}

/**
 * 第四段：发消息和音视频通话按钮区
 */
@Composable
private fun FriendActionButtonsSection(
    onSendMessageClick: () -> Unit,
    onAudioVideoCallClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SpaceVerticalSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium)
    ) {
        // 发消息按钮
        Button(
            onClick = onSendMessageClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = ShapeMedium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chat_bubble),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.start_chat),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // 音视频通话按钮
        FilledTonalButton(
            onClick = onAudioVideoCallClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = ShapeMedium,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_call_phone),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.friend_info_audio_video_call),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}