package com.seanchen.xinchat.feature.contact.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ContactRepository
import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.model.response.UserSummaryResponse
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.feature.contact.R
import com.seanchen.xinchat.feature.contact.state.ContactUiState
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

    init {
        refreshFriends()
    }

    fun updateSearchQuery(value: String) {
        _uiState.update { it.copy(searchQuery = value, errorMessage = null) }
    }

    fun refreshFriends() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = contactRepository.getFriends().asResult(),
            showToast = false,
            onData = { data ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        friends = data.map { user -> user.toUiState() }
                    )
                }
            },
            onError = { message, _ ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = message)
                }
            }
        )
    }

    fun searchUsers() {
        val keyword = uiState.value.searchQuery.trim()
        if (keyword.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), errorMessage = null) }
            return
        }

        _uiState.update { it.copy(isSearching = true, errorMessage = null) }
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = contactRepository.searchUsers(keyword).asResult(),
            showToast = false,
            onData = { data ->
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = data.map { user -> user.toUiState() }
                    )
                }
            },
            onError = { message, _ ->
                _uiState.update {
                    it.copy(isSearching = false, errorMessage = message)
                }
            }
        )
    }

    fun addFriend(user: ContactUserUiState) {
        addFriendByUsername(user.username)
    }

    fun addFriendByUsername(username: String, message: String = "") {
        val keyword = username.trim()
        if (keyword.isBlank()) {
            ToastUtils.showError(R.string.contact_enter_username)
            return
        }

        _uiState.update { it.copy(isSendingFriendRequest = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val matches = contactRepository.searchUsers(keyword).first().data.orEmpty()
                val target = matches.firstOrNull { candidate ->
                    candidate.username.equals(keyword, ignoreCase = true) ||
                        candidate.name.equals(keyword, ignoreCase = true) ||
                        candidate.email.equals(keyword, ignoreCase = true)
                } ?: matches.firstOrNull()

                if (target == null) {
                    ToastUtils.showError(R.string.contact_user_not_found)
                    return@launch
                }

                val response = contactRepository.createFriendRequest(
                    CreateFriendRequest(
                        addresseeId = target.id,
                        message = message.trim()
                    )
                ).first()

                if (response.isSucceeded) {
                    ToastUtils.showSuccess(R.string.friend_request_sent)
                } else {
                    ToastUtils.showError(response.message ?: "发送好友申请失败")
                }
            } catch (exception: Exception) {
                ToastUtils.showError(exception.message ?: "发送好友申请失败")
            } finally {
                _uiState.update { it.copy(isSendingFriendRequest = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun UserSummaryResponse.toUiState(): ContactUserUiState {
        return ContactUserUiState(
            id = id,
            displayName = name.ifBlank { username.ifBlank { email.ifBlank { "未命名" } } },
            username = username,
            email = email,
            avatarColor = avatarColor
        )
    }
}
