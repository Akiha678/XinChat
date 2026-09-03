package com.seanchen.xinchat.feature.contact.state

import com.seanchen.xinchat.feature.contact.model.ContactUserModel

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

fun ContactUserModel.toUiState() = ContactUserUiState(id, displayName, username, email, avatarColor)


sealed class ContactState{
    data object Loading : ContactState()

    data object Success : ContactState()

    data class Error(
        val message: String
    ) : ContactState()
}