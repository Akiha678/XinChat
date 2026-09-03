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
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import com.seanchen.xinchat.feature.contact.state.AddFriendUiState
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
        onAddFriend = viewModel::addFriend)
}

@Composable
internal fun AddFriendScreen(
    state: AddFriendUiState = AddFriendUiState(),
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
        Column(
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(state.username, onValueChange = onUsernameChange, Modifier.fillMaxWidth(), singleLine = true, label = { Text(stringResource(R.string.contact_add_friend_hint)) })
            Button(onClick = onSearch, enabled = state.username.isNotBlank() && !state.isSearching, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.search)) }
            if (state.isSearching) PageLoading()
            state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            LazyColumn(contentPadding = PaddingValues(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(state.results, key = { it.id }) { user ->
                    AppListItem(title = user.displayName, description = user.username.ifBlank { user.email }, showArrow = false, leadingContent = { ContactAvatar(user) }, trailingContent = { TextButton(onClick = { onAddFriend(user) }, enabled = state.sendingUserId == null) { Text(stringResource(R.string.add_friend)) } }, modifier = Modifier.background(MaterialTheme.colorScheme.surface))
                }
            }
        }
    }
}
