package com.seanchen.xinchat.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _verificationCode = MutableStateFlow("")
    val verificationCode: StateFlow<String> = _verificationCode

    private val _isLoadingCode = MutableStateFlow(false)
    val isLoadingCode: StateFlow<Boolean> = _isLoadingCode

    val isEmailValid = _email.map { email ->
        ValidationUtil.isValidEmail(email)
    }

    val isResetEnabled = combine(
        _email,
        _verificationCode,
        _newPassword,
        _confirmPassword
    ) { email, code, newPassword, confirmPassword ->
        ValidationUtil.isValidEmail(email) &&
            ValidationUtil.isValidSmsCode(code) &&
            ValidationUtil.isValidPassword(newPassword) &&
            newPassword == confirmPassword
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updateNewPassword(value: String) {
        _newPassword.value = value
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun updateVerificationCode(value: String) {
        _verificationCode.value = value
    }

    fun sendVerificationCode() {
        if (!ValidationUtil.isValidEmail(_email.value)) {
            ToastUtils.showError(R.string.invalid_email)
            return
        }

        _isLoadingCode.value = true
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.getPasswordCode(mapOf("email" to _email.value)).asResult(),
            onData = { codeOrMessage ->
                if (ValidationUtil.isValidSmsCode(codeOrMessage)) {
                    _verificationCode.value = codeOrMessage
                }
                ToastUtils.showSuccess(R.string.verification_code_sent)
            },
            onFinally = {
                _isLoadingCode.value = false
            }
        )
    }

    fun resetPassword() {
        if (!ValidationUtil.isValidEmail(_email.value)) {
            ToastUtils.showError(R.string.invalid_email)
            return
        }

        if (!ValidationUtil.isValidSmsCode(_verificationCode.value)) {
            ToastUtils.showError(R.string.invalid_verification_code)
            return
        }

        if (!ValidationUtil.isValidPassword(_newPassword.value)) {
            ToastUtils.showError(R.string.invalid_password)
            return
        }

        if (_newPassword.value != _confirmPassword.value) {
            ToastUtils.showError(R.string.password_mismatch)
            return
        }

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.updatePassword(
                mapOf(
                    "email" to _email.value,
                    "code" to _verificationCode.value,
                    "newPassword" to _newPassword.value
                )
            ).asResult(),
            onData = { _ ->
                ToastUtils.showSuccess(R.string.reset_password_success)
                navigateBack()
            }
        )
    }
}
