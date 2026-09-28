package com.seanchen.xinchat.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ChatRepository
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.feature.chat.state.ChatInfoUiState
import com.seanchen.xinchat.feature.chat.util.ChatMessageEventBus
import com.seanchen.xinchat.feature.chat.util.ChatSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatInfoViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val chatSettingsManager: ChatSettingsManager,
    private val chatMessageEventBus: ChatMessageEventBus
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ChatInfoUiState())
    val uiState: StateFlow<ChatInfoUiState> = _uiState.asStateFlow()

    private var currentSessionId: Long = 0L

    fun init(sessionId: Long) {
        if (currentSessionId == sessionId && sessionId > 0) return
        currentSessionId = sessionId

        val isPinned = chatSettingsManager.isPinned(sessionId)
        val isMuted = chatSettingsManager.isMuted(sessionId)

        _uiState.update {
            it.copy(
                sessionId = sessionId,
                isPinned = isPinned,
                isMuted = isMuted,
                isLoading = true
            )
        }

        loadSessionInfo(sessionId)
    }

    private fun loadSessionInfo(sessionId: Long) {
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = chatRepository.getSessions().asResult(),
            onData = { conversations ->
                val session = conversations.find { it.id == sessionId }
                _uiState.update { current ->
                    if (session != null) {
                        current.copy(
                            name = session.name,
                            avatarUrl = session.avatarUrl,
                            peerId = session.peerId,
                            isLoading = false
                        )
                    } else {
                        current.copy(isLoading = false)
                    }
                }
            },
            onError = { _, _ ->
                _uiState.update { it.copy(isLoading = false) }
            }
        )
    }

    fun togglePin(pinned: Boolean) {
        val sessionId = _uiState.value.sessionId
        chatSettingsManager.setPinned(sessionId, pinned)
        _uiState.update { it.copy(isPinned = pinned) }
    }

    fun toggleMute(muted: Boolean) {
        val sessionId = _uiState.value.sessionId
        chatSettingsManager.setMuted(sessionId, muted)
        _uiState.update { it.copy(isMuted = muted) }
    }

    fun clearChatHistory() {
        val sessionId = _uiState.value.sessionId
        chatSettingsManager.clearHistory(sessionId)
        chatMessageEventBus.publishClearSession(sessionId)
    }
}
