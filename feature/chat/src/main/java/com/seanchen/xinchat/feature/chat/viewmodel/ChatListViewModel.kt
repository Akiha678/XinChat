package com.seanchen.xinchat.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ChatRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.Conversation
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.feature.chat.state.ChatListUiState
import com.seanchen.xinchat.feature.chat.state.ChatSessionItemUiState
import com.seanchen.xinchat.feature.chat.util.ChatMessageEventBus
import com.seanchen.xinchat.feature.chat.util.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val appState: AppState,
    private val chatMessageEventBus: ChatMessageEventBus
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<BaseNetWorkUiState<ChatListUiState>>(
        BaseNetWorkUiState.Loading
    )
    val uiState: StateFlow<BaseNetWorkUiState<ChatListUiState>> = _uiState.asStateFlow()

    private val webSocketManager = WebSocketManager()

    init {
        webSocketManager.setOnMessageReceived(::onMessageReceived)
        viewModelScope.launch {
            chatMessageEventBus.sentMessages.collect(::onMessageReceived)
        }
        refreshSessions()
    }

    /**
     * 会话列表页面可见（进入组合或从后台回到前台）时调用：
     * 确保 WebSocket 实时连接可用，并静默拉取会话列表，
     * 补齐连接断开期间（例如停留在聊天详情页、切到其他 Tab 或退到后台时）错过的消息与未读数。
     */
    fun onScreenVisible() {
        connectWebSocket()
        loadSessions(publishLoading = false)
    }

    /**
     * 会话列表页面不可见（被聊天详情页覆盖、切走或应用退到后台）时调用：
     * 断开本页持有的实时连接，避免同一账号同时存在多个连接而相互顶掉。
     */
    fun onScreenHidden() {
        webSocketManager.disconnect()
    }

    /**
     * 显示加载态并重新拉取会话列表（下拉刷新与错误重试入口）。
     */
    fun refreshSessions() {
        loadSessions(publishLoading = true)
    }

    private fun loadSessions(publishLoading: Boolean) {
        if (publishLoading) {
            _uiState.value = BaseNetWorkUiState.Loading
        }
        ResultHandler.handleResult(
            scope = viewModelScope,
            flow = chatRepository.getSessions().asResult(),
            showToast = false,
            onSuccess = { response ->
                if (response.isSucceeded) {
                    _uiState.value = BaseNetWorkUiState.Success(
                        toChatListUiState(response.data.orEmpty())
                    )
                } else {
                    handleLoadFailure(
                        publishLoading = publishLoading,
                        message = response.message ?: "加载消息失败"
                    )
                }
            },
            onError = { message, exception ->
                handleLoadFailure(publishLoading, message, exception)
            }
        )
    }

    /**
     * 静默刷新失败时保留当前列表内容，避免错误页闪烁打断用户。
     */
    private fun handleLoadFailure(
        publishLoading: Boolean,
        message: String,
        exception: Throwable? = null
    ) {
        if (!publishLoading && _uiState.value is BaseNetWorkUiState.Success) {
            return
        }
        _uiState.value = BaseNetWorkUiState.Error(message, exception)
    }

    private fun toChatListUiState(conversations: List<Conversation>): ChatListUiState =
        ChatListUiState(
            sessions = conversations.map { conversation ->
                ChatSessionItemUiState(
                    id = conversation.id,
                    peerId = conversation.peerId,
                    name = conversation.name,
                    preview = conversation.preview,
                    lastMessageAt = conversation.lastMessageAt,
                    unreadCount = conversation.unreadCount,
                    colorSeed = conversation.colorSeed
                )
            }
        )

    /**
     * 会话列表需要在页面未打开具体聊天时也保持实时连接。
     */
    private fun connectWebSocket() {
        val token = appState.auth.value?.token.orEmpty()
        if (token.isNotBlank()) {
            webSocketManager.connect(token, viewModelScope)
        }
    }

    /**
     * 根据实时消息更新最近消息、未读数和会话排序。
     */
    private fun onMessageReceived(message: Msg) {
        val currentState = _uiState.value as? BaseNetWorkUiState.Success
            ?: return
        val currentSessions = currentState.data.sessions
        val session = currentSessions.firstOrNull { it.id == message.sessionId }

        // 新会话可能刚刚由另一端创建，重新拉取列表以获得名称和头像色值。
        if (session == null) {
            refreshSessions()
            return
        }

        val updatedSession = session.copy(
            preview = message.content?.data?.takeIf { it.isNotBlank() } ?: session.preview,
            lastMessageAt = message.createTime ?: session.lastMessageAt,
            unreadCount = if (message.type == 0) {
                session.unreadCount
            } else {
                (session.unreadCount + 1).coerceAtMost(Int.MAX_VALUE)
            }
        )
        val updatedSessions = buildList {
            add(updatedSession)
            addAll(currentSessions.filterNot { it.id == session.id })
        }
        _uiState.value = BaseNetWorkUiState.Success(
            currentState.data.copy(sessions = updatedSessions)
        )
    }

    override fun onCleared() {
        webSocketManager.disconnect()
        super.onCleared()
    }
}
