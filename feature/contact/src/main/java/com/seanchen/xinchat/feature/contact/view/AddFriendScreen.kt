package com.seanchen.xinchat.feature.contact.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.appbar.CenterTopAppBar
import com.seanchen.xinchat.core.ui.component.list.AppListItem
import com.seanchen.xinchat.core.ui.component.loading.PageLoading
import com.seanchen.xinchat.feature.contact.R
import com.seanchen.xinchat.feature.contact.model.AddFriendModel
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import com.seanchen.xinchat.feature.contact.state.AddFriendUiState
import com.seanchen.xinchat.feature.contact.state.SearchUserUiState
import com.seanchen.xinchat.feature.contact.viewmodel.AddFriendViewModel

@Composable
fun AddFriendRoute(
    viewModel: AddFriendViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AddFriendScreen(
        state = state,
        onUsernameChange = viewModel::updateUsername,
        onSearch = viewModel::search,
        onAddFriend = viewModel::addFriend
    )
}

@Composable
internal fun AddFriendScreen(
    state: AddFriendModel = AddFriendModel(),
    onUsernameChange: (String) -> Unit = {},
    onSearch: () -> Unit = {},
    onAddFriend: (ContactUserUiState) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = R.string.contact_add_friend_title,
                onBackClick = ::navigateBack
            )
        }
    ) { padding ->
        AddFriendContentView(
            state = state,
            paddingValues = padding,
            onUsernameChange = onUsernameChange,
            onSearch = onSearch,
            onAddFriend = onAddFriend
        )
    }
}

@Composable
private fun AddFriendContentView(
    state: AddFriendModel,
    paddingValues: PaddingValues,
    onUsernameChange: (String) -> Unit,
    onSearch: () -> Unit,
    onAddFriend: (ContactUserUiState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(stringResource(R.string.contact_add_friend_hint))
            }
        )

        val isSearching = state.searchState is SearchUserUiState.Loading

        Button(
            onClick = onSearch,
            enabled = state.username.isNotBlank() && !isSearching,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.search))
        }

        when (val addFriendState = state.addFriendState) {
            AddFriendUiState.Idle,
            is AddFriendUiState.Loading -> Unit

            is AddFriendUiState.Success -> {
                Text(
                    text = addFriendState.message,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            is AddFriendUiState.Error -> {
                Text(
                    text = addFriendState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        when (val searchState = state.searchState) {
            SearchUserUiState.Idle -> Unit

            SearchUserUiState.Loading -> PageLoading()

            is SearchUserUiState.Success -> {
                if (searchState.users.isEmpty()) {
                    Text(
                        text = stringResource(R.string.contact_search_no_result),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            items = searchState.users,
                            key = { it.id }
                        ) { user ->
                            AddFriendItem(
                                user = user,
                                addFriendState = state.addFriendState,
                                onAddFriend = onAddFriend
                            )
                        }
                    }
                }
            }

            is SearchUserUiState.Error -> {
                Text(
                    text = searchState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Composable
fun AddFriendItem(
    user: ContactUserUiState,
    addFriendState: AddFriendUiState,
    onAddFriend: (ContactUserUiState) -> Unit
){
    val isAdding = addFriendState is AddFriendUiState.Loading
    val isAdded = addFriendState is AddFriendUiState.Success &&
        addFriendState.userId == user.id

    AppListItem(
        title = user.displayName,
        description = user.username.ifBlank {
            user.email
        },
        showArrow = false,
        leadingContent = {
            ContactAvatar(user)
        },
        trailingContent = {
            TextButton(
                onClick = {
                    onAddFriend(user)
                },
                enabled = !isAdding && !isAdded
            ) {
                Text(stringResource(R.string.add_friend))
            }
        },
        modifier = Modifier.background(
            MaterialTheme.colorScheme.surface
        )
    )
}
