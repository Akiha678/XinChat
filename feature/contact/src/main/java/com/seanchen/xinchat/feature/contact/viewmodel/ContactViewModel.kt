package com.seanchen.xinchat.feature.contact.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ContactRepository
import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.feature.contact.R
import com.seanchen.xinchat.feature.contact.model.toContactUserModel
import com.seanchen.xinchat.feature.contact.state.ContactUiState
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import com.seanchen.xinchat.feature.contact.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

    init {
        refreshFriends()
    }

    /**
     * 更新搜索关键词
     */
    fun updateSearchQuery(value: String) {
        _uiState.update { it.copy(searchQuery = value, errorMessage = null) }
        if (value.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    /**
     * 切换搜索栏激活状态
     */
    fun toggleSearch(active: Boolean? = null) {
        _uiState.update { current ->
            val nextActive = active ?: !current.isSearchActive
            current.copy(
                isSearchActive = nextActive,
                searchQuery = if (!nextActive) "" else current.searchQuery,
                searchResults = if (!nextActive) emptyList() else current.searchResults
            )
        }
    }

    /**
     * 切换排序方式（按姓名 或 按在线状态）
     */
    fun toggleSortOrder() {
        _uiState.update { it.copy(sortByOnline = !it.sortByOnline) }
    }

    /**
     * 刷新好友列表
     */
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
                        friends = data.mapIndexed { index, user ->
                            // 模拟 Telegram 逼真的在线状态体验（部分在线，其余展示最近上线时间）
                            val isOnline = index % 3 == 0
                            val lastSeen = when (index % 4) {
                                0 -> "在线"
                                1 -> "刚刚上线"
                                2 -> "今天 10:24"
                                else -> "昨天"
                            }
                            user.toContactUserModel().toUiState(
                                isOnline = isOnline,
                                lastSeenText = if (isOnline) "在线" else lastSeen
                            )
                        }
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

    /**
     * 搜索全网用户
     */
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
                        searchResults = data.map { user -> user.toContactUserModel().toUiState() }
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
            runCatching { ToastUtils.showError(R.string.contact_enter_username) }
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
                    runCatching { ToastUtils.showError(R.string.contact_user_not_found) }
                    return@launch
                }

                val response = contactRepository.createFriendRequest(
                    CreateFriendRequest(
                        addresseeId = target.id,
                        message = message.trim()
                    )
                ).first()

                if (response.isSucceeded) {
                    runCatching { ToastUtils.showSuccess(R.string.friend_request_sent) }
                } else {
                    runCatching { ToastUtils.showError(response.message ?: "发送好友申请失败") }
                }
            } catch (exception: Exception) {
                runCatching { ToastUtils.showError(exception.message ?: "发送好友申请失败") }
            } finally {
                _uiState.update { it.copy(isSendingFriendRequest = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
