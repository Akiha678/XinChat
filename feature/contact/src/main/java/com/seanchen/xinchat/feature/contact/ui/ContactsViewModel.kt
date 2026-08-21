//package com.seanchen.xinchat.feature.contact.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.seanchen.xinchat.core.data.model.Conversation
//import com.seanchen.xinchat.core.data.model.FriendRequest
//import com.seanchen.xinchat.core.data.model.User
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//data class ContactsUiState(
//    val friends: List<User> = emptyList(),
//    val incomingRequests: List<FriendRequest> = emptyList(),
//    val outgoingRequests: List<FriendRequest> = emptyList(),
//    val searchResults: List<User> = emptyList(),
//    val query: String = "",
//    val isLoading: Boolean = true,
//    val isSearching: Boolean = false,
//    val processingId: Long? = null,
//    val errorMessage: String? = null,
//    val openedConversation: Conversation? = null,
//)
//
//@HiltViewModel
//class ContactsViewModel @Inject constructor(
//    private val friendRepository: FriendRepository,
//    private val chatRepository: ChatRepository,
//) : ViewModel() {
//    private val mutableUiState = MutableStateFlow(ContactsUiState())
//    val uiState = mutableUiState.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            friendRepository.friends.collect { friends ->
//                mutableUiState.update { it.copy(friends = friends) }
//            }
//        }
//        viewModelScope.launch {
//            friendRepository.incomingRequests.collect { requests ->
//                mutableUiState.update { it.copy(incomingRequests = requests) }
//            }
//        }
//        viewModelScope.launch {
//            friendRepository.outgoingRequests.collect { requests ->
//                mutableUiState.update { it.copy(outgoingRequests = requests) }
//            }
//        }
//        refresh()
//    }
//
//    fun onQueryChanged(value: String) {
//        mutableUiState.update {
//            it.copy(query = value.take(50), searchResults = emptyList(), errorMessage = null)
//        }
//    }
//
//    fun refresh() {
//        viewModelScope.launch {
//            mutableUiState.update { it.copy(isLoading = true, errorMessage = null) }
//            runCatching { friendRepository.refresh() }
//                .onSuccess { mutableUiState.update { it.copy(isLoading = false) } }
//                .onFailure(::showError)
//        }
//    }
//
//    fun search() {
//        val query = mutableUiState.value.query.trim()
//        if (query.isEmpty()) return
//        viewModelScope.launch {
//            mutableUiState.update { it.copy(isSearching = true, errorMessage = null) }
//            runCatching { friendRepository.search(query) }
//                .onSuccess { users ->
//                    mutableUiState.update { it.copy(searchResults = users, isSearching = false) }
//                }
//                .onFailure(::showError)
//        }
//    }
//
//    fun sendRequest(user: User) = process(user.id) {
//        friendRepository.sendRequest(user.id, "你好，我想添加你为好友")
//        mutableUiState.update { state ->
//            state.copy(searchResults = state.searchResults.filterNot { it.id == user.id })
//        }
//    }
//
//    fun accept(request: FriendRequest) = process(request.id) {
//        friendRepository.acceptRequest(request.id)
//        chatRepository.refreshConversations()
//    }
//
//    fun reject(request: FriendRequest) = process(request.id) {
//        friendRepository.rejectRequest(request.id)
//    }
//
//    fun openConversation(friend: User) = process(friend.id) {
//        val conversation = chatRepository.openDirectConversation(friend.id)
//        mutableUiState.update { it.copy(openedConversation = conversation) }
//    }
//
//    fun consumeOpenedConversation() {
//        mutableUiState.update { it.copy(openedConversation = null) }
//    }
//
//    private fun process(id: Long, action: suspend () -> Unit) {
//        if (mutableUiState.value.processingId != null) return
//        viewModelScope.launch {
//            mutableUiState.update { it.copy(processingId = id, errorMessage = null) }
//            runCatching { action() }
//                .onSuccess { mutableUiState.update { it.copy(processingId = null) } }
//                .onFailure(::showError)
//        }
//    }
//
//    private fun showError(error: Throwable) {
//        mutableUiState.update {
//            it.copy(
//                isLoading = false,
//                isSearching = false,
//                processingId = null,
//                errorMessage = error.message ?: "操作失败",
//            )
//        }
//    }
//}
