package com.seanchen.xinchat.feature.contact.model

data class AddFriendModel(
    val username: String = "",
    val results: List<ContactUserModel> = emptyList(),
    val isSearching: Boolean = false,
    val sendingUserId: Long? = null,
    val errorMessage: String? = null,
)
