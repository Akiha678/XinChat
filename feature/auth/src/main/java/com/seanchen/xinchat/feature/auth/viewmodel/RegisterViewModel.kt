package com.seanchen.xinchat.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = username.length >= 3 && displayName.isNotBlank() && email.contains('@') &&
            password.length >= 6 && password == confirmPassword && !isSubmitting
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(RegisterUiState())
    val uiState = mutableUiState.asStateFlow()

    fun onUsernameChanged(value: String) = update { copy(username = value.take(50)) }
    fun onDisplayNameChanged(value: String) = update { copy(displayName = value.take(80)) }
    fun onEmailChanged(value: String) = update { copy(email = value.take(120)) }
    fun onPasswordChanged(value: String) = update { copy(password = value.take(72)) }
    fun onConfirmPasswordChanged(value: String) = update { copy(confirmPassword = value.take(72)) }

    fun register() {
        val state = mutableUiState.value
        if (!state.canSubmit) return
        viewModelScope.launch {
            mutableUiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            runCatching {
                authRepository.register(
                    username = state.username,
                    displayName = state.displayName,
                    email = state.email,
                    password = state.password,
                )
            }.onFailure { error ->
                mutableUiState.update {
                    it.copy(isSubmitting = false, errorMessage = error.message ?: "注册失败")
                }
            }
        }
    }

    private fun update(transform: RegisterUiState.() -> RegisterUiState) {
        mutableUiState.update { it.transform().copy(errorMessage = null) }
    }
}
