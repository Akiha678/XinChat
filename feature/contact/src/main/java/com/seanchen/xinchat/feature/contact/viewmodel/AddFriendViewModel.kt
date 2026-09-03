package com.seanchen.xinchat.feature.contact.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.ContactRepository
import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.feature.contact.model.AddFriendModel
import com.seanchen.xinchat.feature.contact.model.toContactUserModel
import com.seanchen.xinchat.feature.contact.state.ContactUserUiState
import com.seanchen.xinchat.feature.contact.state.AddFriendUiState
import com.seanchen.xinchat.feature.contact.state.SearchUserUiState
import com.seanchen.xinchat.feature.contact.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val repository: ContactRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(AddFriendModel())
    val uiState: StateFlow<AddFriendModel> = _uiState.asStateFlow()

    /**
     * 更新搜索用户名
     */
    fun updateUsername(value: String) {
        _uiState.update {
            it.copy(
                username = value,
                searchState = SearchUserUiState.Idle
            )
        }
    }

    /**
     * 搜索用户
     */
    fun search() {
        if (_uiState.value.searchState is SearchUserUiState.Loading) {
            return
        }

        val username = uiState.value.username.trim()

        if (username.isBlank()) {
            return
        }

        _uiState.update {
            it.copy(searchState = SearchUserUiState.Loading)
        }

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = repository.searchUsers(username).asResult(),
            showToast = false,

            onLoading = {
                _uiState.update {
                    it.copy(
                        searchState = SearchUserUiState.Loading
                    )
                }
            },

            onData = { users ->
                val uiUsers = users.map {
                    it.toContactUserModel().toUiState()
                }

                _uiState.update {
                    it.copy(
                        searchState = SearchUserUiState.Success(
                            users = uiUsers
                        )
                    )
                }
            },

            onError = { message, _ ->
                _uiState.update {
                    it.copy(
                        searchState = SearchUserUiState.Error(
                            message = message
                        )
                    )
                }
            }
        )
    }

    /**
     * 添加好友
     */
    fun addFriend(user: ContactUserUiState) {
        if (_uiState.value.addFriendState is AddFriendUiState.Loading) {
            return
        }

        _uiState.update {
            it.copy(
                addFriendState = AddFriendUiState.Loading(userId = user.id)
            )
        }

        // 好友申请只通过 ResultHandler 发起一次，避免重复创建申请。
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = repository.createFriendRequest(
                CreateFriendRequest(
                    addresseeId = user.id
                )
            ).asResult(),
            showToast = false,

            onLoading = {
                _uiState.update {
                    it.copy(
                        addFriendState = AddFriendUiState.Loading(
                            userId = user.id
                        )
                    )
                }
            },

            onData = { response ->
                _uiState.update {
                    it.copy(
                        addFriendState = AddFriendUiState.Success(
                            userId = user.id,
                            message = response.message ?: "好友申请已发送"
                        )
                    )
                }
            },

            onError = { message, _ ->
                _uiState.update {
                    it.copy(
                        addFriendState = AddFriendUiState.Error(
                            userId = user.id,
                            message = message
                        )
                    )
                }
            }
        )
    }

    /**
     * 重置添加好友状态
     */
    fun resetAddFriendState(){
        _uiState.update {
            it.copy(
                addFriendState = AddFriendUiState.Idle
            )
        }
    }
}
