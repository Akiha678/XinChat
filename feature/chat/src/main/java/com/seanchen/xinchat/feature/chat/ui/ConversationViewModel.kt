//package com.seanchen.xinchat.feature.chat.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.seanchen.xinchat.core.data.model.ChatMessage
//import com.seanchen.xinchat.core.data.repository.AuthRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//data class ConversationUiState(
//    val messages: List<ChatMessage> = emptyList(),
//    val currentUserId: Long? = null,
//    val draft: String = "",
//    val isLoading: Boolean = true,
//    val isSending: Boolean = false,
//    val errorMessage: String? = null,
//)
//
//@HiltViewModel
//class ConversationViewModel @Inject constructor(
//    private val chatRepository: ChatRepository,
//    authRepository: AuthRepository,
//) : ViewModel() {
//    private val mutableUiState = MutableStateFlow(ConversationUiState())
//    val uiState = mutableUiState.asStateFlow()
//    private var conversationId: Long? = null
//    private var messagesJob: Job? = null
//
//    init {
//        viewModelScope.launch {
//            authRepository.session.collect { session ->
//                mutableUiState.update { it.copy(currentUserId = session?.user?.id) }
//            }
//        }
//    }
//
//    fun load(id: Long) {
//        if (conversationId == id) return
//        conversationId = id
//        messagesJob?.cancel()
//        messagesJob = viewModelScope.launch {
//            launch {
//                chatRepository.messages(id).collect { messages ->
//                    mutableUiState.update { it.copy(messages = messages) }
//                }
//            }
//            mutableUiState.update { it.copy(isLoading = true, errorMessage = null) }
//            runCatching {
//                chatRepository.refreshMessages(id)
//                chatRepository.markRead(id)
//            }.onSuccess {
//                mutableUiState.update { it.copy(isLoading = false) }
//            }.onFailure { error ->
//                mutableUiState.update {
//                    it.copy(isLoading = false, errorMessage = error.message ?: "加载消息失败")
//                }
//            }
//        }
//    }
//
//    fun onDraftChanged(value: String) {
//        if (value.length <= MAX_MESSAGE_LENGTH) {
//            mutableUiState.update { it.copy(draft = value, errorMessage = null) }
//        }
//    }
//
//    fun send() {
//        val id = conversationId ?: return
//        val content = mutableUiState.value.draft.trim()
//        if (content.isEmpty() || mutableUiState.value.isSending) return
//        viewModelScope.launch {
//            mutableUiState.update { it.copy(isSending = true, errorMessage = null) }
//            runCatching { chatRepository.sendMessage(id, content) }
//                .onSuccess { mutableUiState.update { it.copy(draft = "", isSending = false) } }
//                .onFailure { error ->
//                    mutableUiState.update {
//                        it.copy(isSending = false, errorMessage = error.message ?: "消息发送失败")
//                    }
//                }
//        }
//    }
//
//    private companion object {
//        const val MAX_MESSAGE_LENGTH = 2_000
//    }
//}
