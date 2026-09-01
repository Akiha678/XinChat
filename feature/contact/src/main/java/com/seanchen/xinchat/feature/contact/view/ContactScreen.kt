package com.seanchen.xinchat.feature.contact.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.designsystem.theme.CommonIcon
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingLarge
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.navigation.chat.ChatNavigator
import com.seanchen.xinchat.core.ui.R as CoreUiR
import com.seanchen.xinchat.core.ui.component.appbar.CenterTopAppBar
import com.seanchen.xinchat.core.ui.component.empty.Empty
import com.seanchen.xinchat.core.ui.component.loading.PageLoading
import com.seanchen.xinchat.core.ui.component.list.AppListItem
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextSize
import com.seanchen.xinchat.core.ui.component.text.TextType
import com.seanchen.xinchat.core.ui.component.title.TitleWithLine
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
        onSearchClick = viewModel::searchUsers,
        onFriendClick = { ChatNavigator.toChatMessage() },
        onAddFriendClick = viewModel::addFriend,
        onAddFriendByUsername = viewModel::addFriendByUsername,
        onRefresh = viewModel::refreshFriends,
        onClearError = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ContactScreen(
    uiState: ContactUiState = ContactUiState(),
    onSearchQueryChange: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onFriendClick: (ContactUserUiState) -> Unit = {},
    onAddFriendClick: (ContactUserUiState) -> Unit = {},
    onAddFriendByUsername: (String, String) -> Unit = { _, _ -> },
    onRefresh: () -> Unit = {},
    onClearError: () -> Unit = {},
) {
    var showAddFriendDialog by rememberSaveable { mutableStateOf(false) }
    var addUsername by rememberSaveable { mutableStateOf("") }
    var addMessage by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = R.string.contacts_title,
                showBackIcon = false,
                actions = {
                    IconButton(
                        onClick = onSearchClick,
                        enabled = !uiState.isSearching && !uiState.isSendingFriendRequest
                    ) {
                        CommonIcon(
                            resId = CoreUiR.drawable.ic_search,
                            size = 22.dp,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { showAddFriendDialog = true },
                        enabled = !uiState.isSendingFriendRequest
                    ) {
                        CommonIcon(
                            resId = CoreUiR.drawable.ic_add,
                            size = 22.dp,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        ContactContentView(
            uiState = uiState,
            paddingValues = paddingValues,
            onSearchQueryChange = onSearchQueryChange,
            onSearchClick = {
                focusManager.clearFocus()
                onSearchClick()
            },
            isSendingFriendRequest = uiState.isSendingFriendRequest,
            onFriendClick = onFriendClick,
            onAddFriendClick = onAddFriendClick,
            onRefresh = onRefresh,
            onClearError = onClearError
        )
    }

    if (showAddFriendDialog) {
        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = { Text(text = stringResource(id = R.string.contact_add_friend_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(SpaceVerticalSmall)) {
                    OutlinedTextField(
                        value = addUsername,
                        onValueChange = { addUsername = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text(text = stringResource(id = R.string.contact_add_friend_hint)) }
                    )
                    OutlinedTextField(
                        value = addMessage,
                        onValueChange = { addMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(id = R.string.contact_add_friend_message_hint)) },
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = addUsername.isNotBlank() && !uiState.isSendingFriendRequest,
                    onClick = {
                        onAddFriendByUsername(addUsername, addMessage)
                        showAddFriendDialog = false
                        addUsername = ""
                        addMessage = ""
                    }
                ) {
                    Text(text = stringResource(id = R.string.add_friend))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog = false }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun ContactContentView(
    uiState: ContactUiState,
    paddingValues: PaddingValues,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    isSendingFriendRequest: Boolean,
    onFriendClick: (ContactUserUiState) -> Unit,
    onAddFriendClick: (ContactUserUiState) -> Unit,
    onRefresh: () -> Unit,
    onClearError: () -> Unit,
) {
    when {
        uiState.isLoading && uiState.friends.isEmpty() -> {
            PageLoading()
        }

        uiState.friends.isEmpty() && uiState.searchResults.isEmpty() && uiState.errorMessage != null -> {
            Empty(
                message = R.string.contacts_load_failed,
                retryButtonText = R.string.retry,
                onRetryClick = onRefresh
            )
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(
                    start = SpacePaddingMedium,
                    end = SpacePaddingMedium,
                    top = paddingValues.calculateTopPadding() + SpacePaddingMedium,
                    bottom = paddingValues.calculateBottomPadding() + SpacePaddingMedium
                ),
                verticalArrangement = Arrangement.spacedBy(SpaceVerticalMedium)
            ) {
                if (uiState.errorMessage != null && uiState.friends.isNotEmpty()) {
                    item {
                        ContactErrorBanner(
                            message = uiState.errorMessage,
                            onClear = onClearError
                        )
                    }
                }

                if (uiState.searchQuery.isNotBlank()) {
                    item {
                        TitleWithLine(
                            text = stringResource(id = R.string.search_results)
                        )
                    }

                    when {
                        uiState.isSearching -> {
                            item {
                                PageLoading()
                            }
                        }

                        uiState.searchResults.isEmpty() -> {
                            item {
                                Empty(
                                    message = R.string.contact_search_no_result,
                                    icon = CoreUiR.drawable.ic_empty_data
                                )
                            }
                        }

                        else -> {
                            items(
                                items = uiState.searchResults,
                                key = { it.id }
                            ) { user ->
                                ContactSearchResultItem(
                                    user = user,
                                    enabled = !isSendingFriendRequest,
                                    onAddFriendClick = onAddFriendClick
                                )
                            }
                        }
                    }
                }

                item {
                    TitleWithLine(
                        text = stringResource(id = R.string.my_friends)
                    )
                }

                if (uiState.friends.isEmpty()) {
                    item {
                        Empty(
                            message = R.string.friends_empty_title,
                            subtitle = R.string.friends_empty_description,
                            icon = CoreUiR.drawable.ic_empty_data,
                            onRetryClick = onRefresh
                        )
                    }
                } else {
                    items(
                        items = uiState.friends,
                        key = { it.id }
                    ) { user ->
                        ContactFriendItem(
                            user = user,
                            onClick = onFriendClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactFriendItem(
    user: ContactUserUiState,
    onClick: (ContactUserUiState) -> Unit
) {
    AppListItem(
        title = user.displayName,
        description = user.username.ifBlank { user.email },
        showArrow = true,
        leadingContent = {
            ContactAvatar(user = user)
        },
        onClick = { onClick(user) }
    )
}

@Composable
private fun ContactSearchResultItem(
    user: ContactUserUiState,
    enabled: Boolean = true,
    onAddFriendClick: (ContactUserUiState) -> Unit
) {
    AppListItem(
        title = user.displayName,
        description = user.username.ifBlank { user.email },
        showArrow = false,
        leadingContent = {
            ContactAvatar(user = user)
        },
        trailingContent = {
            IconButton(
                onClick = { onAddFriendClick(user) },
                enabled = enabled
            ) {
                CommonIcon(
                    resId = CoreUiR.drawable.ic_add,
                    size = 20.dp,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        onClick = if (enabled) { { onAddFriendClick(user) } } else { {} }
    )
}

@Composable
private fun ContactErrorBanner(
    message: String,
    onClear: () -> Unit
) {
    Card {
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
                    resId = CoreUiR.drawable.ic_close,
                    size = 20.dp,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ContactAvatar(
    user: ContactUserUiState,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (user.avatarColor == 0) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color(user.avatarColor)
    }
    val contentColor = if (user.avatarColor == 0) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        Color.White
    }
    val initial = user.displayName.take(1).ifBlank { "?" }

    Box(
        modifier = modifier
            .size(48.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = contentColor,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactScreenPreview() {
    MaterialTheme {
        ContactScreen(
            uiState = ContactUiState(
                searchQuery = "sean",
                friends = listOf(
                    ContactUserUiState(
                        id = 1,
                        displayName = "Sean",
                        username = "seanchen",
                        email = "sean@example.com",
                        avatarColor = 0xFF4E73FF.toInt()
                    )
                ),
                searchResults = listOf(
                    ContactUserUiState(
                        id = 2,
                        displayName = "Akiha",
                        username = "akiha",
                        email = "akiha@example.com",
                        avatarColor = 0xFF00B894.toInt()
                    )
                )
            )
        )
    }
}
