package com.seanchen.xinchat.feature.contact.state

data class AddFriendUiState(
    val username: String = "",
    val results: List<ContactUserUiState> = emptyList(),
    val isSearching: Boolean = false,
    val sendingUserId: Long? = null,
    val errorMessage: String? = null,
)


sealed class AddFriendState {
    /**
     * 初始状态
     */
    data object Idle: AddFriendState()

    /**
     * 添加中
     */
    data object Loading: AddFriendState()

    /**
     * 添加成功
     */
    data object Success: AddFriendState()

    /**
     * 添加失败
     */
    data class Error(
        val message: String
    ) : AddFriendState()
}