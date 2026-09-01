package com.seanchen.xinchat.feature.user.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appState: AppState,
) : BaseViewModel() {
    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

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

    suspend fun logout() {
        _isLoggingOut.value = true
        try {
            appState.logout()
        } finally {
            _isLoggingOut.value = false
        }
    }
}
