package com.seanchen.xinchat.feature.contact.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.designsystem.theme.CommonIcon
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingLarge
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingMedium
import com.seanchen.xinchat.core.navigation.chat.ChatNavigator
import com.seanchen.xinchat.core.navigation.contact.ContactNavigator
import com.seanchen.widget.ui.empty.Empty
import com.seanchen.widget.ui.loading.PageLoading
import com.seanchen.widget.ui.text.AppText
import com.seanchen.widget.ui.text.TextSize
import com.seanchen.widget.ui.text.TextType
import com.seanchen.xinchat.feature.contact.R
import com.seanchen.xinchat.feature.contact.state.ContactUiState
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import com.seanchen.xinchat.feature.contact.viewmodel.ContactViewModel
import kotlin.math.abs

/**
 * Telegram 经典头像色盘
 */
private val TelegramAvatarColors = listOf(
    Color(0xFFE17076), // 珊瑚红
    Color(0xFFFAA774), // 暖橙色
    Color(0xFFA695E7), // 薰衣草紫
    Color(0xFF7BC862), // 清爽绿
    Color(0xFF6EC9CB), // 绿松石青
    Color(0xFF65AADD), // 浅蔚蓝
    Color(0xFFEE7AAE), // 樱花粉
)

private val TelegramOnlineColor = Color(0xFF00C853)
private val TelegramActionBlue = Color(0xFF2AABEE)
private val TelegramActionGreen = Color(0xFF4CAF50)
private val TelegramActionPurple = Color(0xFF7E57C2)

@Composable
fun ContactRoute(
    viewModel: ContactViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContactScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onToggleSearch = viewModel::toggleSearch,
        onToggleSort = viewModel::toggleSortOrder,
        onFriendClick = { user ->
            ChatNavigator.toChatMessage(sessionId = user.id)
        },
        onAddContactClick = ContactNavigator::toAddFriend,
        onRefresh = viewModel::refreshFriends,
        onClearError = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ContactScreen(
    uiState: ContactUiState = ContactUiState(),
    onSearchQueryChange: (String) -> Unit = {},
    onToggleSearch: (Boolean?) -> Unit = {},
    onToggleSort: () -> Unit = {},
    onFriendClick: (ContactUserUiState) -> Unit = {},
    onAddContactClick: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onClearError: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TelegramContactTopAppBar(
                uiState = uiState,
                onSearchQueryChange = onSearchQueryChange,
                onToggleSearch = onToggleSearch,
                onToggleSort = onToggleSort,
                onAddContactClick = onAddContactClick
            )
        },
        floatingActionButton = {
            if (!uiState.isSearchActive) {
                FloatingActionButton(
                    onClick = onAddContactClick,
                    containerColor = TelegramActionBlue,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(16.dp)
                ) {
                    CommonIcon(
                        resId = R.drawable.ic_add,
                        size = 24.dp,
                        tint = Color.White
                    )
                }
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        ContactContentView(
            uiState = uiState,
            paddingValues = paddingValues,
            onFriendClick = onFriendClick,
            onAddContactClick = onAddContactClick,
            onRefresh = onRefresh,
            onClearError = onClearError
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TelegramContactTopAppBar(
    uiState: ContactUiState,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: (Boolean?) -> Unit,
    onToggleSort: () -> Unit,
    onAddContactClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = stringResource(R.string.contacts_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (uiState.friends.isNotEmpty() && !uiState.isSearchActive) {
                        Text(
                            text = stringResource(R.string.contacts_count, uiState.friends.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            actions = {
                // 搜索按钮
                IconButton(onClick = { onToggleSearch(null) }) {
                    CommonIcon(
                        resId = if (uiState.isSearchActive) R.drawable.ic_close else R.drawable.ic_search,
                        size = 22.dp,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                // 排序按钮
                if (!uiState.isSearchActive && uiState.friends.isNotEmpty()) {
                    IconButton(onClick = onToggleSort) {
                        CommonIcon(
                            resId = R.drawable.ic_menu_list,
                            size = 20.dp,
                            tint = if (uiState.sortByOnline) TelegramActionBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // 搜索输入栏展开动画
        AnimatedVisibility(
            visible = uiState.isSearchActive,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            TelegramSearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun TelegramSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonIcon(
            resId = R.drawable.ic_search,
            size = 18.dp,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(TelegramActionBlue),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.weight(1f)
        ) { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_contacts),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 15.sp
                    )
                }
                innerTextField()
            }
        }
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(24.dp)
            ) {
                CommonIcon(
                    resId = R.drawable.ic_close,
                    size = 16.dp,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ContactContentView(
    uiState: ContactUiState,
    paddingValues: PaddingValues,
    onFriendClick: (ContactUserUiState) -> Unit,
    onAddContactClick: () -> Unit,
    onRefresh: () -> Unit,
    onClearError: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(paddingValues)
    ) {
        when {
            uiState.isLoading && uiState.friends.isEmpty() -> {
                PageLoading()
            }

            uiState.friends.isEmpty() && uiState.errorMessage != null -> {
                Empty(
                    message = R.string.contacts_load_failed,
                    retryButtonText = R.string.retry,
                    onRetryClick = onRefresh
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 错误横幅
                    if (uiState.errorMessage != null) {
                        item {
                            ContactErrorBanner(
                                message = uiState.errorMessage,
                                onClear = onClearError
                            )
                        }
                    }

                    // Telegram 风格顶部操作项（非搜索模式下展示）
                    if (!uiState.isSearchActive) {
                        item {
                            TelegramActionItem(
                                title = stringResource(R.string.find_people_nearby),
                                iconRes = R.drawable.ic_search,
                                iconBgColor = TelegramActionPurple,
                                onClick = onAddContactClick
                            )
                        }
                        item {
                            TelegramActionItem(
                                title = stringResource(R.string.invite_friends),
                                iconRes = R.drawable.ic_add,
                                iconBgColor = TelegramActionGreen,
                                onClick = onAddContactClick
                            )
                        }
                        item {
                            TelegramActionItem(
                                title = stringResource(R.string.add_contact),
                                iconRes = R.drawable.ic_add,
                                iconBgColor = TelegramActionBlue,
                                onClick = onAddContactClick
                            )
                        }
                        item {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                thickness = 8.dp
                            )
                        }
                    }

                    // 空状态展示
                    if (uiState.displayedFriends.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Empty(
                                    message = if (uiState.searchQuery.isNotBlank()) {
                                        R.string.contact_search_no_result
                                    } else {
                                        R.string.friends_empty_title
                                    },
                                    subtitle = if (uiState.searchQuery.isNotBlank()) null else R.string.friends_empty_description,
                                    icon = R.drawable.ic_empty_data,
                                    onRetryClick = onRefresh
                                )
                            }
                        }
                    } else {
                        // 按字母分组展示联系人
                        uiState.groupedFriends.forEach { (section, contacts) ->
                            // 粘性分组头
                            item(key = "section_$section") {
                                TelegramSectionHeader(letter = section)
                            }

                            items(
                                items = contacts,
                                key = { it.id }
                            ) { user ->
                                TelegramContactRow(
                                    user = user,
                                    onClick = { onFriendClick(user) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelegramActionItem(
    title: String,
    iconRes: Int,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CommonIcon(
                resId = iconRes,
                size = 20.dp,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TelegramSectionHeader(letter: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TelegramActionBlue
            )
        )
    }
}

@Composable
private fun TelegramContactRow(
    user: ContactUserUiState,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Telegram 风格头像（带在线状态小绿点）
        TelegramAvatar(user = user)

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (user.isOnline) stringResource(R.string.status_online) else user.lastSeenText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (user.isOnline) TelegramOnlineColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TelegramAvatar(
    user: ContactUserUiState,
    modifier: Modifier = Modifier
) {
    val bgColor = remember(user.id, user.displayName) {
        if (user.avatarColor != 0) {
            Color(user.avatarColor)
        } else {
            val hash = abs(user.id.hashCode() + user.displayName.hashCode())
            TelegramAvatarColors[hash % TelegramAvatarColors.size]
        }
    }
    val initial = user.displayName.trim().take(1).uppercase().ifBlank { "?" }

    Box(
        modifier = modifier.size(46.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 在线状态指示圆点
        if (user.isOnline) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd)
                    .background(TelegramOnlineColor, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}

/**
 * 兼容旧组件名调用
 */
@Composable
fun ContactAvatar(
    user: ContactUserUiState,
    modifier: Modifier = Modifier
) {
    TelegramAvatar(user = user, modifier = modifier)
}

@Composable
private fun ContactErrorBanner(
    message: String,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacePaddingLarge, vertical = SpacePaddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText(
                text = message,
                type = TextType.ERROR,
                size = TextSize.BODY_MEDIUM,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClear) {
                CommonIcon(
                    resId = R.drawable.ic_close,
                    size = 20.dp,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Preview(name = "Telegram 风格联系人列表", showBackground = true)
@Composable
private fun ContactScreenPreview() {
    MaterialTheme {
        val sampleUsers = listOf(
            ContactUserUiState(1, "Alice Walker", "alice", "alice@test.com", 0, isOnline = true, sectionLetter = "A"),
            ContactUserUiState(2, "Andrew Garfield", "andrew", "andrew@test.com", 0, isOnline = false, lastSeenText = "昨天 18:30", sectionLetter = "A"),
            ContactUserUiState(3, "Bob Dylan", "bob", "bob@test.com", 0, isOnline = false, lastSeenText = "今天 09:12", sectionLetter = "B"),
            ContactUserUiState(4, "Charlie Brown", "charlie", "charlie@test.com", 0, isOnline = true, sectionLetter = "C"),
            ContactUserUiState(5, "David Miller", "david", "david@test.com", 0, isOnline = false, lastSeenText = "最近上线", sectionLetter = "D"),
            ContactUserUiState(6, "张三", "zhangsan", "zs@test.com", 0, isOnline = true, sectionLetter = "Z"),
            ContactUserUiState(7, "李四", "lisi", "ls@test.com", 0, isOnline = false, lastSeenText = "刚刚上线", sectionLetter = "L")
        )
        ContactScreen(
            uiState = ContactUiState(
                friends = sampleUsers
            )
        )
    }
}

@Preview(name = "搜索模式过滤中", showBackground = true)
@Composable
private fun ContactScreenSearchPreview() {
    MaterialTheme {
        val sampleUsers = listOf(
            ContactUserUiState(1, "Alice Walker", "alice", "alice@test.com", 0, isOnline = true, sectionLetter = "A"),
            ContactUserUiState(2, "Andrew Garfield", "andrew", "andrew@test.com", 0, isOnline = false, sectionLetter = "A")
        )
        ContactScreen(
            uiState = ContactUiState(
                isSearchActive = true,
                searchQuery = "Al",
                friends = sampleUsers
            )
        )
    }
}

@Preview(name = "空联系人状态", showBackground = true)
@Composable
private fun ContactScreenEmptyPreview() {
    MaterialTheme {
        ContactScreen(
            uiState = ContactUiState()
        )
    }
}
