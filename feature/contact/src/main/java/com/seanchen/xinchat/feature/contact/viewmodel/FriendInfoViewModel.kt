package com.seanchen.xinchat.feature.contact.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ChatRepository
import com.seanchen.xinchat.core.data.repository.ContactRepository
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.feature.contact.state.FriendInfoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendInfoViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val chatRepository: ChatRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(FriendInfoUiState())
    val uiState: StateFlow<FriendInfoUiState> = _uiState.asStateFlow()

    // 加载朋友信息
    fun loadFriendInfo(userId: Long) {
        if (userId <= 0L) return
        _uiState.update { it.copy(id = userId) }
        viewModelScope.launch {
            contactRepository.getFriends().collect { response ->
                val friend = response.data?.find { it.id == userId }
                if (friend != null) {
                    _uiState.update { current ->
                        current.copy(
                            id = friend.id,
                            displayName = friend.name.ifBlank { friend.username.ifBlank { friend.email.ifBlank { "未命名" } } },
                            username = friend.username,
                            avatarUrl = friend.avatarUrl,
                            avatarColor = friend.avatarColor
                        )
                    }
                }
            }
        }
    }

    /**
     * 发起与好友聊天：创建或获取单聊会话成功后回调真正的 sessionId
     */
    fun startChatWithFriend(friendId: Long, onSessionReady: (Long) -> Unit) {
        if (friendId <= 0L) return
        viewModelScope.launch {
            try {
                val response = chatRepository.createDirectConversation(friendId).first()
                val conversation = response.data
                if (response.isSucceeded && conversation != null) {
                    onSessionReady(conversation.id)
                } else {
                    runCatching { ToastUtils.showError(response.message ?: "创建会话失败") }
                }
            } catch (exception: Exception) {
                runCatching { ToastUtils.showError(exception.message ?: "创建会话失败") }
            }
        }
    }
}
