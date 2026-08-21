//package com.seanchen.xinchat.feature.contact.ui
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//
//@Composable
//internal fun ContactsRoute(
//    onConversationOpened: (conversationId: Long, name: String) -> Unit,
//    viewModel: ContactsViewModel = hiltViewModel(),
//) {
//    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
//    LaunchedEffect(uiState.openedConversation) {
//        uiState.openedConversation?.let { conversation ->
//            onConversationOpened(conversation.id, conversation.name)
//            viewModel.consumeOpenedConversation()
//        }
//    }
//    ContactsScreen(
//        uiState = uiState,
//        onQueryChanged = viewModel::onQueryChanged,
//        onSearch = viewModel::search,
//        onSendRequest = viewModel::sendRequest,
//        onAccept = viewModel::accept,
//        onReject = viewModel::reject,
//        onFriendClick = viewModel::openConversation,
//        onRefresh = viewModel::refresh,
//    )
//}
