package com.seanchen.xinchat.feature.contact.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ContactRepository
import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import com.seanchen.xinchat.feature.contact.state.AddFriendUiState
import com.seanchen.xinchat.feature.contact.model.toContactUserModel
import com.seanchen.xinchat.feature.contact.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val repository: ContactRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(AddFriendUiState())
    val uiState: StateFlow<AddFriendUiState> = _uiState.asStateFlow()

    fun updateUsername(value: String) = _uiState.update { it.copy(username = value, errorMessage = null) }

    fun search() {
        val username = uiState.value.username.trim()
        if (username.isBlank()) return
        _uiState.update { it.copy(isSearching = true, results = emptyList(), errorMessage = null) }
        ResultHandler.handleResultWithData(viewModelScope, repository.searchUsers(username).asResult(), showToast = false,
            onData = { users -> _uiState.update { it.copy(isSearching = false, results = users.map { user -> user.toContactUserModel().toUiState() }) } },
            onError = { message, _ -> _uiState.update { it.copy(isSearching = false, errorMessage = message) } })
    }

    fun addFriend(user: ContactUserUiState) {
        _uiState.update { it.copy(sendingUserId = user.id, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = repository.createFriendRequest(CreateFriendRequest(addresseeId = user.id)).first()
                _uiState.update { it.copy(sendingUserId = null, errorMessage = response.message ?: if (response.isSucceeded) "好友申请已发送" else "发送好友申请失败") }
            } catch (e: Exception) {
                _uiState.update { it.copy(sendingUserId = null, errorMessage = e.message ?: "发送好友申请失败") }
            }
        }
    }
}
