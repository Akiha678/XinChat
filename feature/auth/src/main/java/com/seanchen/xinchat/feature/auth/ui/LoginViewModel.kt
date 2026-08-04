package com.seanchen.xinchat.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(LoginUiState())
    val uiState = mutableUiState.asStateFlow()

    fun onUsernameChanged(value: String) {
        mutableUiState.update { it.copy(username = value.take(MAX_USERNAME_LENGTH), errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        mutableUiState.update { it.copy(password = value.take(MAX_PASSWORD_LENGTH), errorMessage = null) }
    }

    fun login() {
        val state = mutableUiState.value
        if (state.username.isBlank() || state.password.isBlank() || state.isSubmitting) return
        viewModelScope.launch {
            mutableUiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            runCatching { authRepository.login(state.username, state.password) }
                .onFailure { error ->
                    mutableUiState.update {
                        it.copy(isSubmitting = false, errorMessage = error.message ?: "登录失败")
                    }
                }
        }
    }

    private companion object {
        const val MAX_USERNAME_LENGTH = 50
        const val MAX_PASSWORD_LENGTH = 72
    }
}
