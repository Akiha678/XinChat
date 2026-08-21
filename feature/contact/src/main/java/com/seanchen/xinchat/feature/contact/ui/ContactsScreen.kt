//package com.seanchen.xinchat.feature.contact.ui
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.seanchen.xinchat.core.data.model.FriendRequest
//import com.seanchen.xinchat.core.data.model.FriendRequestStatus
//import com.seanchen.xinchat.core.data.model.User
//import com.seanchen.xinchat.feature.contact.R
//
//@Composable
//internal fun ContactsScreen(
//    uiState: ContactsUiState,
//    onQueryChanged: (String) -> Unit,
//    onSearch: () -> Unit,
//    onSendRequest: (User) -> Unit,
//    onAccept: (FriendRequest) -> Unit,
//    onReject: (FriendRequest) -> Unit,
//    onFriendClick: (User) -> Unit,
//    onRefresh: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    LazyColumn(modifier.fillMaxSize()) {
//        item {
//            Text(
//                stringResource(R.string.contacts_title),
//                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
//                style = MaterialTheme.typography.headlineMedium,
//            )
//        }
//        item {
//            Row(
//                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//            ) {
//                OutlinedTextField(
//                    value = uiState.query,
//                    onValueChange = onQueryChanged,
//                    modifier = Modifier.weight(1f),
//                    label = { Text(stringResource(R.string.search_username)) },
//                    singleLine = true,
//                )
//                Button(onClick = onSearch, enabled = uiState.query.isNotBlank() && !uiState.isSearching) {
//                    if (uiState.isSearching) CircularProgressIndicator(strokeWidth = 2.dp)
//                    else Text(stringResource(R.string.search))
//                }
//            }
//        }
//        uiState.errorMessage?.let { message ->
//            item {
//                Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
//                    Text(message, color = MaterialTheme.colorScheme.error)
//                    OutlinedButton(onClick = onRefresh) { Text(stringResource(R.string.retry)) }
//                }
//            }
//        }
//        if (uiState.searchResults.isNotEmpty()) {
//            item { SectionTitle(stringResource(R.string.search_results)) }
//            items(uiState.searchResults, key = { "search-${it.id}" }) { user ->
//                UserRow(
//                    user = user,
//                    trailing = {
//                        Button(
//                            onClick = { onSendRequest(user) },
//                            enabled = uiState.processingId == null,
//                        ) { Text(stringResource(R.string.add_friend)) }
//                    },
//                )
//            }
//        }
//        item { SectionTitle(stringResource(R.string.received_requests)) }
//        if (uiState.incomingRequests.isEmpty()) {
//            item { EmptyRow(stringResource(R.string.no_received_requests)) }
//        } else {
//            items(uiState.incomingRequests, key = { "incoming-${it.id}" }) { request ->
//                RequestRow(
//                    request = request,
//                    user = request.requester,
//                    actions = {
//                        OutlinedButton(
//                            onClick = { onReject(request) },
//                            enabled = uiState.processingId == null,
//                        ) { Text(stringResource(R.string.reject)) }
//                        Button(
//                            onClick = { onAccept(request) },
//                            enabled = uiState.processingId == null,
//                        ) { Text(stringResource(R.string.accept)) }
//                    },
//                )
//            }
//        }
//        item { SectionTitle(stringResource(R.string.sent_requests)) }
//        if (uiState.outgoingRequests.isEmpty()) {
//            item { EmptyRow(stringResource(R.string.no_sent_requests)) }
//        } else {
//            items(uiState.outgoingRequests, key = { "outgoing-${it.id}" }) { request ->
//                RequestRow(
//                    request = request,
//                    user = request.addressee,
//                    actions = { Text(statusText(request.status)) },
//                )
//            }
//        }
//        item { SectionTitle(stringResource(R.string.my_friends)) }
//        if (uiState.isLoading && uiState.friends.isEmpty()) {
//            item {
//                Row(Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.Center) {
//                    CircularProgressIndicator()
//                }
//            }
//        } else if (uiState.friends.isEmpty()) {
//            item { EmptyRow(stringResource(R.string.contacts_empty_description)) }
//        } else {
//            items(uiState.friends, key = { "friend-${it.id}" }) { friend ->
//                UserRow(
//                    user = friend,
//                    modifier = Modifier.clickable { onFriendClick(friend) },
//                    trailing = { Text(stringResource(R.string.start_chat)) },
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun SectionTitle(text: String) {
//    Text(
//        text,
//        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
//        style = MaterialTheme.typography.titleMedium,
//        fontWeight = FontWeight.Bold,
//        color = MaterialTheme.colorScheme.primary,
//    )
//}
//
//@Composable
//private fun UserRow(
//    user: User,
//    modifier: Modifier = Modifier,
//    trailing: @Composable () -> Unit,
//) {
//    Row(
//        modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//    ) {
//        Text(user.displayName.take(1), style = MaterialTheme.typography.titleLarge)
//        Column(Modifier.weight(1f)) {
//            Text(user.displayName, style = MaterialTheme.typography.titleSmall)
//            Text("@${user.username}", color = MaterialTheme.colorScheme.onSurfaceVariant)
//        }
//        trailing()
//    }
//    HorizontalDivider()
//}
//
//@Composable
//private fun RequestRow(
//    request: FriendRequest,
//    user: User,
//    actions: @Composable () -> Unit,
//) {
//    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Column(Modifier.weight(1f)) {
//                Text(user.displayName, style = MaterialTheme.typography.titleSmall)
//                Text("@${user.username}", color = MaterialTheme.colorScheme.onSurfaceVariant)
//                request.message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
//            }
//            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { actions() }
//        }
//    }
//}
//
//@Composable
//private fun EmptyRow(text: String) {
//    Text(
//        text,
//        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
//        color = MaterialTheme.colorScheme.onSurfaceVariant,
//    )
//}
//
//@Composable
//private fun statusText(status: FriendRequestStatus): String = when (status) {
//    FriendRequestStatus.PENDING -> stringResource(R.string.status_pending)
//    FriendRequestStatus.ACCEPTED -> stringResource(R.string.status_accepted)
//    FriendRequestStatus.REJECTED -> stringResource(R.string.status_rejected)
//}
