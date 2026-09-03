package com.seanchen.xinchat.feature.contact.model

import com.seanchen.xinchat.feature.contact.state.AddFriendUiState
import com.seanchen.xinchat.feature.contact.state.SearchUserUiState

data class AddFriendModel(
    val username: String = "",
    val searchState: SearchUserUiState = SearchUserUiState.Idle,
    val addFriendState: AddFriendUiState = AddFriendUiState.Idle,
)
