package com.seanchen.xinchat.feature.contact.state

sealed class AddFriendUiState {
    /**
     * 初始状态
     */
    data object Idle : AddFriendUiState()

    /**
     * 添加中
     */
    data class Loading(
        val userId: Long
    ) : AddFriendUiState()

    /**
     * 添加成功
     */
    data class Success(
        val userId: Long,
        val message: String
    ) : AddFriendUiState()

    /**
     * 添加失败
     */
    data class Error(
        val userId: Long,
        val message: String
    ) : AddFriendUiState()
}

sealed class SearchUserUiState {
    /**
     * 尚未搜索
     */
    data object Idle : SearchUserUiState()

    /**
     * 正在搜索
     */
    data object Loading : SearchUserUiState()

    /**
     * 搜索成功
     */
    data class Success(
        val users: List<ContactUserUiState>
    ) : SearchUserUiState()

    /**
     * 搜索失败
     */
    data class Error(
        val message: String
    ) : SearchUserUiState()
}
