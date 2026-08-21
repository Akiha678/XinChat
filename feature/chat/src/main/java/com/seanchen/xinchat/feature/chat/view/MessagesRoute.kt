//package com.seanchen.xinchat.feature.chat.ui
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.seanchen.xinchat.feature.chat.navigation.ConversationKey
//
//@Composable
//internal fun MessagesRoute(
//    onConversationClick: (ConversationKey) -> Unit,
//    viewModel: MessagesViewModel = hiltViewModel(),
//) {
//    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
//    val snackbarHostState = remember { SnackbarHostState() }
//    LaunchedEffect(uiState.notification) {
//        uiState.notification?.let { message ->
//            snackbarHostState.showSnackbar(message)
//            viewModel.consumeNotification()
//        }
//    }
//    Box(Modifier.fillMaxSize()) {
//        MessagesScreen(
//            uiState = uiState,
//            onConversationClick = { conversation ->
//                onConversationClick(ConversationKey(conversation.id, conversation.name))
//            },
//            onRefresh = viewModel::refresh,
//        )
//        SnackbarHost(hostState = snackbarHostState)
//    }
//}
