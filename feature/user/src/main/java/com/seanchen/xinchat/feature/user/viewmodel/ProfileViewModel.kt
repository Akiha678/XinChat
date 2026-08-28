package com.seanchen.xinchat.feature.user.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.UserInfoRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.util.log.LogUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appState: AppState,
    private val userInfoRepository: UserInfoRepository

) : BaseViewModel(), DefaultLifecycleObserver {
    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    private val _logoutCompleted = MutableStateFlow(false)
    val logoutCompleted: StateFlow<Boolean> = _logoutCompleted.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = appState.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun logout() {
        if (_isLoggingOut.value) {
            return
        }

        viewModelScope.launch {
            _isLoggingOut.value = true
            try {
                val response = userInfoRepository.logoff(emptyMap<String, Any>()).first()
                if (!response.isSucceeded) {
                    LogUtils.w("远端退出登录失败: ${response.message}")
                }
            } catch (exception: Exception) {
                LogUtils.w(exception, "远端退出登录异常")
            } finally {
                appState.logout()
                _isLoggingOut.value = false
                _logoutCompleted.value = true
            }
        }
    }

}
