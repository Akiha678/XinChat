package com.seanchen.xinchat.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.response.LoginResponse
import com.seanchen.xinchat.core.navigation.NavigationOptions
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.core.navigation.navigate
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val appState: AppState,
    private val authRepository: AuthRepository
) : BaseViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _verificationCode = MutableStateFlow("")
    val verificationCode: StateFlow<String> = _verificationCode

    private val _isLoadingCaptcha = MutableStateFlow(false)
    val isLoadingCaptcha: StateFlow<Boolean> = _isLoadingCaptcha

    val isEmailValid = _email.map { email ->
        ValidationUtil.isValidEmail(email)
    }

    val isRegisterEnabled = combine(
        _email,
        _verificationCode,
        _password,
        _confirmPassword
    ) { email, code, password, confirmPassword ->
        ValidationUtil.isValidEmail(email) &&
            ValidationUtil.isValidSmsCode(code) &&
            ValidationUtil.isValidPassword(password) &&
            password == confirmPassword
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun updateVerificationCode(value: String) {
        _verificationCode.value = value
    }

    fun onSendCodeButtonClick() {
        if (!ValidationUtil.isValidEmail(_email.value)) {
            ToastUtils.showError(R.string.invalid_email)
            return
        }
        sendVerificationCode()
    }

    fun sendVerificationCode() {
        val params = mapOf("email" to email.value)

        _isLoadingCaptcha.value = true
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.getRegisterCode(params).asResult(),
            onData = { codeOrMessage ->
                if (ValidationUtil.isValidSmsCode(codeOrMessage)) {
                    _verificationCode.value = codeOrMessage
                }
                ToastUtils.showSuccess(R.string.verification_code_sent)
            },
            onFinally = {
                _isLoadingCaptcha.value = false
            }
        )
    }

    fun register() {
        if (!ValidationUtil.isValidEmail(_email.value)) {
            ToastUtils.showError(R.string.invalid_email)
            return
        }

        if (!ValidationUtil.isValidSmsCode(_verificationCode.value)) {
            ToastUtils.showError(R.string.invalid_verification_code)
            return
        }

        if (!ValidationUtil.isValidPassword(_password.value)) {
            ToastUtils.showError(R.string.invalid_password)
            return
        }

        if (_password.value != _confirmPassword.value) {
            ToastUtils.showError(R.string.password_mismatch)
            return
        }

        val params = mapOf(
            "email" to email.value,
            "code" to verificationCode.value,
            "password" to password.value
        )

        viewModelScope.launch {
            try {
                val response = authRepository.register(params)
                registerSuccess(response)
            } catch (exception: Exception) {
                exception.message?.let(ToastUtils::showError) ?: ToastUtils.showError(R.string.register_failed)
            }
        }
    }

    private fun registerSuccess(response: LoginResponse) {
        viewModelScope.launch {
            ToastUtils.showSuccess(R.string.register_success)
            appState.updateAuth(response.toAuth())
            appState.refreshUserInfo()
            navigate(
                route = MainRoutes.Main,
                navOptions = NavigationOptions(
                    popUpToRoute = AuthRoutes.Login,
                    inclusive = true,
                    allowPopToEmpty = true
                )
            )
        }
    }

    private fun LoginResponse.toAuth(): Auth {
        val expiresAtInstant = runCatching {
            Instant.parse(expiresAt)
        }.getOrElse {
            Instant.now().plusSeconds(7 * 24 * 60 * 60)
        }
        val expireSeconds = Duration.between(Instant.now(), expiresAtInstant)
            .seconds
            .coerceAtLeast(1L)

        return Auth(
            token = accessToken,
            refreshToken = accessToken,
            expire = expireSeconds,
            refreshExpire = expireSeconds,
            createdAt = System.currentTimeMillis()
        )
    }
}
