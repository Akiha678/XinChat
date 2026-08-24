package com.seanchen.xinchat.feature.chat.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.state.BaseNetWorkUiState
import com.seanchen.xinchat.core.common.base.state.LoadMoreState
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ChatRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.Msg
import com.seanchen.xinchat.core.model.request.MessagePageRequest
import com.seanchen.xinchat.core.model.request.ReadMessageRequest
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.core.util.log.LogUtils
import com.seanchen.xinchat.feature.chat.state.WebSocketConnectionState
import com.seanchen.xinchat.feature.chat.util.ChatSoundManager
import com.seanchen.xinchat.feature.chat.util.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ChatViewModel"

@HiltViewModel
class ChatMessageViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val appState: AppState,
    @param:ApplicationContext private val context: Context,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<BaseNetWorkUiState<Unit>>(BaseNetWorkUiState.Loading)
    val uiState: StateFlow<BaseNetWorkUiState<Unit>> = _uiState.asStateFlow()

    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory.asStateFlow()

    private val _loadMoreState = MutableStateFlow<LoadMoreState>(LoadMoreState.PullToLoad)
    val loadMoreState: StateFlow<LoadMoreState> = _loadMoreState.asStateFlow()

    private val _newMessageIds = MutableStateFlow<Set<Long>>(emptySet())
    val newMessageIds: StateFlow<Set<Long>> = _newMessageIds.asStateFlow()

    private val _newMessageEvent = MutableSharedFlow<Unit>()
    val newMessageEvent: SharedFlow<Unit> = _newMessageEvent.asSharedFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _connectionState = MutableStateFlow<WebSocketConnectionState>(
        WebSocketConnectionState.Disconnected)

    private val _sessionId = MutableStateFlow<Long>(0)

    private val webSocketManager = WebSocketManager()

    // 回调接口
    private var onMessageReceived: ((Msg) -> Unit)? = null

    private val chatSoundManager = ChatSoundManager(context)

    private var currentPage = 1
    private val pageSize = 10
    private var hasMoreData = true


    /**
     * 聊天消息列表
     */
    private val _messages = MutableStateFlow<List<Msg>>(emptyList())
    val messages: StateFlow<List<Msg>> = _messages.asStateFlow()


    /**
     * 页面加载开始时间
     */
    private var loadingStartTime = 0L

    /**
     * 首次页面加载最少展示时间
     */
    private val minLoadingTime = 320L

    init {
        setupWebSocketCallbacks()
        createSession()
    }

    /**
     * 设置WebSocket回调
     */
    private fun setupWebSocketCallbacks(){
        webSocketManager.setOnMessageReceived { message ->
            addNewMessage(message)
        }

        webSocketManager.setOnConnectionStateChanged { state ->
            _connectionState.value = state
        }
    }

    private fun createSession() {
        beginLoading()
        LogUtils.d(TAG, "开始创建会话")

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = chatRepository.createSession().asResult(),
            onData = { session ->
                _sessionId.value = session.id
                LogUtils.d(TAG, "会话创建成功: sessionId = ${session.id}")

                connectWebSocket()

                loadHistoryMessages(isInitialLoad = true)

                markMessagesAsRead()
            },
            onError = { message, exception ->
                LogUtils.e(TAG, "会话创建失败: $message")
                viewModelScope.launch {
                    applyMinLoadingDelay()
                    _uiState.value = BaseNetWorkUiState.Error(message, exception)
                    _connectionState.value = WebSocketConnectionState.Error("创建会话失败")
                }
            }
        )
    }

    fun loadHistoryMessages(isInitialLoad: Boolean = false) {
        if (_isLoadingHistory.value) return

        val sessionId = _sessionId.value
        if (sessionId <= 0) return

        LogUtils.d(TAG, "开始加载历史消息: sessionId = $sessionId")
        _isLoadingHistory.value = true

        // 如果是加载更多，设置为加载状态
        if (currentPage > 1){
            _loadMoreState.value = LoadMoreState.Loading
        }

        val params = MessagePageRequest(
            sessionId = sessionId,
            page = currentPage,
            size = pageSize
        )

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = chatRepository.getMessagePage(params).asResult(),
            onData = { data ->
                val newMessage = data.list ?: emptyList()
                val pagination = data.pagination

                LogUtils.d(TAG, "历史消息加载成功: ${newMessage.size}条消息")
                hasMoreData = if (pagination != null) {
                    val total = pagination.total ?: 0
                    val size = pagination.size ?: currentPage
                    val currentPageNum = pagination.page ?: currentPage
                    size * currentPageNum < total
                } else {
                    newMessage.size >= pageSize
                }

                if (currentPage == 1) {
                    _messages.value = newMessage
                } else {
                    val currentMessage = _messages.value
                    val existingIds = currentMessage.map { it.id }.toSet()
                    val uniqueNewMessages = newMessage.filter { it.id !in existingIds }
                    _messages.value = currentMessage + uniqueNewMessages
                }

                _isLoadingHistory.value = false

                if (isInitialLoad) {
                    viewModelScope.launch {
                        applyMinLoadingDelay()
                        _uiState.value = BaseNetWorkUiState.Success(Unit)
                    }
                }
                _loadMoreState.value =
                    if (hasMoreData) LoadMoreState.PullToLoad else LoadMoreState.NoMore
            },
            onError = { message, exception ->
                LogUtils.e(TAG, "历史消息加载失败: $message")
                _isLoadingHistory.value = false

                if (currentPage > 1) {
                    // 加载失败回退页码
                    currentPage--
                    _loadMoreState.value = LoadMoreState.Error
                } else if (isInitialLoad) {
                    viewModelScope.launch {
                        applyMinLoadingDelay()
                        _uiState.value = BaseNetWorkUiState.Error(message, exception)
                    }
                }
            }
        )
    }

    /**
     * 加载更多历史消息
     */
    fun loadMoreMessages() {
        if (_loadMoreState.value == LoadMoreState.Loading ||
            _loadMoreState.value == LoadMoreState.NoMore ||
            !hasMoreData
        ) {
            return
        }

        currentPage++
        loadHistoryMessages()
    }

    /**
     * 建立WebSocket连接
     */
    fun connectWebSocket(){
        val token = appState.auth.value?.token ?: ""
        webSocketManager.connect(token, viewModelScope)
    }

    /**
     * 添加新消息到列表
     */
    private fun addNewMessage(message: Msg) {
        val currentMessages = _messages.value.toMutableList()

        if (currentMessages.none { it.id == message.id }) {
            currentMessages.add(0, message)
            _messages.value = currentMessages

            _newMessageIds.value += message.id

            if (message.type == 1) {
                chatSoundManager.playMessageReceivedSound()
            }
            viewModelScope.launch {
                _newMessageEvent.emit(Unit)
            }
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun clearMessageAnimation(messageId: Long) {
        _newMessageIds.value -= messageId
    }


    fun sendMessage(content: String = _inputText.value, type: String = "text") {
        if (content.isBlank()) return

        val sessionId = _sessionId.value
        if (sessionId <= 0) {
            LogUtils.e(TAG, "发送消息失败: 无效的会话ID")
            return
        }

        if (!webSocketManager.isConnected()) {
            LogUtils.e(TAG, "发送消息失败: WebSocket未连接")
            connectWebSocket() // 尝试重新连接
            return
        }

        // 通过WebSocketManager发送消息
        val success = webSocketManager.sendMessage(sessionId, content, type)
        if (success) {
            LogUtils.d(TAG, "消息发送成功")
            // 播放发送消息音效
            chatSoundManager.playMessageSentSound()
            // 清空输入框
            _inputText.value = ""
        } else {
            // 尝试重新连接
            connectWebSocket()
        }
    }

    /**
     * 标记消息为已读
     *
     * @author Joker.X
     */
    fun markMessagesAsRead() {
        viewModelScope.launch {
            // 获取未读消息ID列表
            val unreadMessages = _messages.value.filter { it.status == 0 }
            if (unreadMessages.isEmpty()) return@launch

            val ids = unreadMessages.map { it.id }
            LogUtils.d(TAG, "标记消息已读: ${ids.joinToString()}")
            val request = ReadMessageRequest(ids)

            try {
                chatRepository.readMessage(request).first()
                LogUtils.d(TAG, "消息已标记为已读")
            } catch (e: Exception) {
                LogUtils.e(TAG, "标记消息已读失败", e)
            }
        }
    }

    fun retryRequest(){
        if (_uiState.value is BaseNetWorkUiState.Error) {
            beginLoading()
        }
        createSession()
    }

    /**
     * 开始页面加载流程
     */
    private fun beginLoading() {
        _uiState.value = BaseNetWorkUiState.Loading
        loadingStartTime = System.currentTimeMillis()
    }

    /**
     * 应用最少加载时间
     */
    private suspend fun applyMinLoadingDelay() {
        val elapsedTime = System.currentTimeMillis() - loadingStartTime
        val remainingTime = (minLoadingTime - elapsedTime).coerceAtLeast(0L)
        if (remainingTime > 0L) {
            delay(remainingTime)
        }
    }

    /**
     * 断开WebSocket连接
     */
    fun disconnectWebSocket(){
        webSocketManager.disconnect()
    }

    @SuppressLint("EmptySuperCall")
    override fun onCleared() {
        disconnectWebSocket()
        chatSoundManager.release()
        super.onCleared()
    }
}