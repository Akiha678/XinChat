package com.seanchen.xinchat.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ResetPasswordViewModel @Inject constructor() : BaseViewModel() {
    /**
     * 手机号输入
     */
    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    /**
     * 新密码输入
     */
    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    /**
     * 确认密码
     */
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    /**
     * 验证码输入
     */
    private val _verificationCode = MutableStateFlow("")
    val verificationCode: StateFlow<String> = _verificationCode

    fun updatePhone(value: String) {
        _phone.value = value
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
        viewModelScope.launch {

        }
    }

    fun resetPassword() {
        viewModelScope.launch {

        }
    }
}