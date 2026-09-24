package com.seanchen.xinchat.feature.contact.view

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.designsystem.theme.CommonIcon
import com.seanchen.xinchat.core.navigation.chat.ChatNavigator
import com.seanchen.widget.ui.appbar.ExpandableSearchTopAppBar
import com.seanchen.widget.ui.banner.AppBanner
import com.seanchen.widget.ui.empty.Empty
import com.seanchen.widget.ui.image.LetterAvatar
import com.seanchen.widget.ui.loading.PageLoading
import com.seanchen.widget.ui.theme.ColorOnline
import com.seanchen.xinchat.feature.contact.R
import com.seanchen.xinchat.feature.contact.state.ContactUiState
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import com.seanchen.xinchat.feature.contact.viewmodel.ContactViewModel



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
        onSearchUsers = viewModel::searchUsers,
        onAddFriend = viewModel::addFriend,
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
    onSearchUsers: () -> Unit = {},
    onAddFriend: (ContactUserUiState) -> Unit = {},
    onRefresh: () -> Unit = {},
    onClearError: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            ExpandableSearchTopAppBar(
                titleText = stringResource(R.string.contacts_title),
                subtitleText = if (uiState.friends.isNotEmpty() && !uiState.isSearchActive) {
                    stringResource(R.string.contacts_count, uiState.friends.size)
                } else null,
                isSearchActive = uiState.isSearchActive,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onSearch = onSearchUsers,
                searchPlaceholder = stringResource(R.string.search_username),
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
                                tint = if (uiState.sortByOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        ContactContentView(
            uiState = uiState,
            paddingValues = paddingValues,
            onFriendClick = onFriendClick,
            onAddFriend = onAddFriend,
            onRefresh = onRefresh,
            onClearError = onClearError
        )
    }
}

@Composable
private fun ContactContentView(
    uiState: ContactUiState,
    paddingValues: PaddingValues,
    onFriendClick: (ContactUserUiState) -> Unit,
    onAddFriend: (ContactUserUiState) -> Unit,
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
                            AppBanner(
                                message = uiState.errorMessage,
                                onClose = onClearError
                            )
                        }
                    }

                    // 搜索模式：展示全网用户搜索结果（可添加好友）
                    if (uiState.isSearchActive && uiState.searchQuery.isNotBlank()) {
                        when {
                            uiState.isSearching -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        PageLoading()
                                    }
                                }
                            }

                            uiState.searchResults.isNotEmpty() -> {
                                item(key = "search_results_header") {
                                    SearchResultsHeader()
                                }

                                items(
                                    items = uiState.searchResults,
                                    key = { it.id }
                                ) { user ->
                                    TelegramSearchResultRow(
                                        user = user,
                                        isFriend = uiState.friends.any { it.id == user.id },
                                        isAdding = uiState.isSendingFriendRequest,
                                        onAddFriend = { onAddFriend(user) }
                                    )
                                }
                            }

                            else -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Empty(
                                            message = R.string.contact_user_not_exist,
                                            icon = R.drawable.ic_empty_data
                                        )
                                    }
                                }
                            }
                        }
                        if (uiState.searchResults.isNotEmpty()) {
                            item {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    thickness = 8.dp
                                )
                            }
                        }
                    }

                    // 空状态展示（仅非搜索模式；搜索模式下的空提示由上方搜索区块统一负责）
                    val showFriendsEmptyState =
                        !uiState.isSearchActive || uiState.searchQuery.isBlank()
                    if (showFriendsEmptyState && uiState.displayedFriends.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Empty(
                                    message = R.string.friends_empty_title,
                                    subtitle = R.string.friends_empty_description,
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
private fun SearchResultsHeader() {
    Text(
        text = stringResource(R.string.search_results),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun TelegramSearchResultRow(
    user: ContactUserUiState,
    isFriend: Boolean,
    isAdding: Boolean,
    onAddFriend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                text = user.username.ifBlank { user.email },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isFriend) {
            Text(
                text = stringResource(R.string.contact_already_friend),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            TextButton(
                onClick = onAddFriend,
                enabled = !isAdding
            ) {
                Text(stringResource(R.string.add_friend))
            }
        }
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
                color = MaterialTheme.colorScheme.primary
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
                color = if (user.isOnline) ColorOnline else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
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
    LetterAvatar(
        name = user.displayName,
        modifier = modifier,
        size = 46.dp,
        backgroundColor = if (user.avatarColor != 0) Color(user.avatarColor) else null,
        isOnline = user.isOnline
    )
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
                searchResults = sampleUsers.take(2),
                friends = sampleUsers
            )
        )
    }
}

@Preview(name = "搜索全网用户结果", showBackground = true)
@Composable
private fun ContactScreenSearchUsersPreview() {
    MaterialTheme {
        val searchUsers = listOf(
            ContactUserUiState(101, "Alice Walker", "alice_walker", "alice@test.com", 0, sectionLetter = "A"),
            ContactUserUiState(102, "Andy Liu", "andy_liu", "andy@test.com", 0, sectionLetter = "A")
        )
        ContactScreen(
            uiState = ContactUiState(
                isSearchActive = true,
                searchQuery = "alice",
                searchResults = searchUsers,
                friends = emptyList()
            )
        )
    }
}

@Preview(name = "搜索结果含好友与非好友", showBackground = true)
@Composable
private fun ContactScreenSearchMixedPreview() {
    MaterialTheme {
        val friends = listOf(
            ContactUserUiState(101, "Alice Walker", "alice_walker", "alice@test.com", 0, sectionLetter = "A")
        )
        val searchUsers = listOf(
            ContactUserUiState(101, "Alice Walker", "alice_walker", "alice@test.com", 0, sectionLetter = "A"),
            ContactUserUiState(103, "Bob Dylan", "bob", "bob@test.com", 0, sectionLetter = "B")
        )
        ContactScreen(
            uiState = ContactUiState(
                isSearchActive = true,
                searchQuery = "a",
                searchResults = searchUsers,
                friends = friends
            )
        )
    }
}

@Preview(name = "搜索用户不存在", showBackground = true)
@Composable
private fun ContactScreenSearchNotFoundPreview() {
    MaterialTheme {
        ContactScreen(
            uiState = ContactUiState(
                isSearchActive = true,
                searchQuery = "not_exist_user"
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
