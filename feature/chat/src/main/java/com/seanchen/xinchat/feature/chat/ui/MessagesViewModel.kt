package com.seanchen.xinchat.feature.chat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.data.model.Conversation
import com.seanchen.xinchat.core.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MessagesUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val notification: String? = null,
)

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(MessagesUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.conversations.collect { conversations ->
                mutableUiState.update { it.copy(conversations = conversations) }
            }
        }
        viewModelScope.launch {
            chatRepository.incomingMessages.collect { message ->
                val sender = chatRepository.conversations.value
                    .firstOrNull { it.id == message.conversationId }
                    ?.name
                    ?: "新消息"
                mutableUiState.update {
                    it.copy(notification = "$sender：${message.content}")
                }
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { chatRepository.refreshConversations() }
                .onSuccess { mutableUiState.update { it.copy(isLoading = false) } }
                .onFailure { error ->
                    mutableUiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "加载会话失败")
                    }
                }
        }
    }

    fun consumeNotification() {
        mutableUiState.update { it.copy(notification = null) }
    }
}
