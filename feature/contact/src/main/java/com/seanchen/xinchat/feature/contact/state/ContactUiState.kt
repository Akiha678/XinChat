package com.seanchen.xinchat.feature.contact.state

data class ContactUiState(
    val searchQuery: String = "",
    val friends: List<ContactUserUiState> = emptyList(),
    val searchResults: List<ContactUserUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val isSendingFriendRequest: Boolean = false,
    val errorMessage: String? = null,
)

data class ContactUserUiState(
    val id: Long,
    val displayName: String,
    val username: String,
    val email: String,
    val avatarColor: Int,
)
