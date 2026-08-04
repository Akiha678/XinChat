package com.seanchen.xinchat.feature.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun ConversationRoute(
    conversationId: Long,
    conversationName: String,
    onBack: () -> Unit,
    viewModel: ConversationViewModel = hiltViewModel(),
) {
    LaunchedEffect(conversationId) { viewModel.load(conversationId) }
    ConversationScreen(
        conversationName = conversationName,
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onDraftChanged = viewModel::onDraftChanged,
        onSend = viewModel::send,
        onBack = onBack,
    )
}
