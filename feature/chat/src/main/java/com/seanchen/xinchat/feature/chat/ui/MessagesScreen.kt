package com.seanchen.xinchat.feature.chat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.data.model.Conversation
import com.seanchen.xinchat.feature.chat.R

@Composable
internal fun MessagesScreen(
    uiState: MessagesUiState,
    onConversationClick: (Conversation) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.messages_title),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            style = MaterialTheme.typography.headlineMedium,
        )
        when {
            uiState.isLoading && uiState.conversations.isEmpty() -> LoadingContent()
            uiState.errorMessage != null && uiState.conversations.isEmpty() -> ErrorContent(
                message = uiState.errorMessage,
                onRetry = onRefresh,
            )
            uiState.conversations.isEmpty() -> EmptyContent()
            else -> LazyColumn(Modifier.fillMaxSize()) {
                items(uiState.conversations, key = Conversation::id) { conversation ->
                    ConversationRow(conversation, onClick = { onConversationClick(conversation) })
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(conversation: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            modifier = Modifier.size(48.dp).clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(conversation.name.take(1), style = MaterialTheme.typography.titleLarge)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(
                conversation.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Medium,
            )
            Text(
                conversation.preview,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (conversation.unreadCount > 0) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                Text(
                    text = conversation.unreadCount.coerceAtMost(99).toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            stringResource(R.string.messages_empty_description),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.size(12.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}
