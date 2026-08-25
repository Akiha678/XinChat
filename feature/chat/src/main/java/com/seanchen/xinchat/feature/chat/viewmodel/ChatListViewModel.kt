package com.seanchen.xinchat.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ChatRepository
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.feature.chat.state.ChatListUiState
import com.seanchen.xinchat.feature.chat.state.ChatSessionItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<BaseNetWorkUiState<ChatListUiState>>(
        BaseNetWorkUiState.Loading
    )
    val uiState: StateFlow<BaseNetWorkUiState<ChatListUiState>> = _uiState.asStateFlow()

    init {
        refreshSessions()
    }

    fun refreshSessions() {
        _uiState.value = BaseNetWorkUiState.Loading
        ResultHandler.handleResult(
            scope = viewModelScope,
            flow = chatRepository.getUnreadCount().asResult(),
            showToast = false,
            onSuccess = { response ->
                if (response.isSucceeded) {
                    _uiState.value = BaseNetWorkUiState.Success(
                        ChatListUiState(
                            sessions = listOf(
                                ChatSessionItemUiState(
                                    id = CUSTOMER_SERVICE_SESSION_ID,
                                    unreadCount = response.data ?: 0
                                )
                            )
                        )
                    )
                }
            },
            onError = { message, exception ->
                _uiState.value = BaseNetWorkUiState.Error(message, exception)
            }
        )
    }

    private companion object {
        const val CUSTOMER_SERVICE_SESSION_ID = 1L
    }
}
