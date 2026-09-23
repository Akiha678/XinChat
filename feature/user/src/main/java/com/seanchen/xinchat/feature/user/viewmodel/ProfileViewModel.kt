package com.seanchen.xinchat.feature.user.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.UserInfoRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appState: AppState,
    private val userInfoRepository: UserInfoRepository,
) : BaseViewModel() {
    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    private val _isUploadingAvatar = MutableStateFlow(false)
    val isUploadingAvatar: StateFlow<Boolean> = _isUploadingAvatar.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = appState.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val userInfo: StateFlow<User?> = appState.userInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        if (appState.isLoggedIn.value && appState.userInfo.value == null) {
            appState.refreshUserInfo()
        }
    }

    /**
     * 上传并更换用户头像
     */
    fun uploadAvatar(
        part: MultipartBody.Part,
        onSuccess: () -> Unit = {},
        onError: (String?) -> Unit = {}
    ) {
        if (_isUploadingAvatar.value) return
        _isUploadingAvatar.value = true
        viewModelScope.launch {
            try {
                userInfoRepository.uploadAvatar(part)
                    .catch { throwable ->
                        _isUploadingAvatar.value = false
                        onError(throwable.message)
                    }
                    .collect { response ->
                        _isUploadingAvatar.value = false
                        val user = response.data
                        if (response.isSucceeded && user != null) {
                            appState.updateUserInfo(user)
                            onSuccess()
                        } else {
                            onError(response.message)
                        }
                    }
            } catch (e: Exception) {
                _isUploadingAvatar.value = false
                onError(e.message)
            }
        }
    }

    suspend fun logout() {
        _isLoggingOut.value = true
        try {
            appState.logout()
        } finally {
            _isLoggingOut.value = false
        }
    }
}
