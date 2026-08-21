package com.seanchen.xinchat.feature.auth.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.core.util.notification.NotificationUtil
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val appState: AppState,
    private val authRepository: AuthRepository,
    @param:ApplicationContext private val context: Context
) : BaseViewModel() {
    /**
     * 手机号输入
     */
    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    /**
     * 密码输入
     */
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    /**
     * 确认密码输入
     */
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    /**
     * 验证码输入
     */
    private val _verificationCode = MutableStateFlow("")
    val verificationCode: StateFlow<String> = _verificationCode

    /**
     * 图片验证码 Popup 是否展示
     */
    private val _showImageCodePopup = MutableStateFlow(false)
    val showImageCodePopup: StateFlow<Boolean> = _showImageCodePopup

    /**
     * 图片验证码
     */
    private val _captcha = MutableStateFlow(Captcha())
    val captcha: StateFlow<Captcha> = _captcha

    /**
     * 图形验证码
     */
    private val _imageCode = MutableStateFlow("")
    val imageCode: StateFlow<String> = _imageCode

    /**
     * 验证码加载状态
     */
    private val _isLoadingCaptcha = MutableStateFlow(false)
    val isLoadingCaptcha: StateFlow<Boolean> = _isLoadingCaptcha

    /**
     * 手机号是否有效
     */
    val isPhoneValid = _phone.map { phone ->
        ValidationUtil.isValidPhone(phone)
    }

    /**
     * 注册按钮是否可用
     */
    val isRegisterEnabled = combine(
        _phone,
        _verificationCode,
        _password,
        _confirmPassword
    ) { phone, code, password, confirmPassword ->
        ValidationUtil.isValidPhone(phone) &&
                ValidationUtil.isValidSmsCode(code) &&
                ValidationUtil.isValidPassword(password) &&
                password == confirmPassword
    }

    /**
     * 更新手机号输入
     */
    fun updatePhone(value: String) {
        _phone.value = value
    }

    /**
     * 更新密码输入
     */
    fun updatePassword(value: String) {
        _password.value = value
    }

    /**
     * 更新确认密码输入
     */
    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    /**
     * 更新验证码输入
     */
    fun updateVerificationCode(value: String) {
        _verificationCode.value = value
    }

    /**
     * 更新图形验证码输入
     */
    fun updateImageCode(value: String) {
        _imageCode.value = value
    }

    /**
     * 显示图片验证码
     */
    fun onSendCodeButtonClick() {
        if (!ValidationUtil.isValidPhone(_phone.value)) {
            ToastUtils.showError(R.string.invalid_phone_number)
            return
        }

        viewModelScope.launch {
            _isLoadingCaptcha.value = true
            fetchCaptcha()
            _isLoadingCaptcha.value = false
            _showImageCodePopup.value = true
        }
    }

    /**
     * 隐藏图片验证码
     */
    fun onHideImageCodePopup() {
        _showImageCodePopup.value = false
        _imageCode.value = ""
    }

    /**
     * 验证码确认
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun onImageCodeConfirm(imageCode: String) {
        updateImageCode(imageCode)
        sendVerificationCode()
    }

    /**
     * 发送短信验证码
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendVerificationCode() {
        val currentImageCode = imageCode.value
        onHideImageCodePopup()

        val params = mapOf(
            "phone" to phone.value,
            "captchaId" to captcha.value.captchaId,
            "code" to currentImageCode
        )

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.getSmsCode(params).asResult(),
            onData = { smsCode ->
                NotificationUtil.sendVerificationCodeNotification(
                    context = context,
                    code = smsCode
                )
            }
        )
    }

    /**
     * 获取图片验证码
     */
    fun getCaptcha() {
        viewModelScope.launch {
            _isLoadingCaptcha.value = true
            fetchCaptcha()
            _isLoadingCaptcha.value = false
        }
    }

    /**
     * 实际获取验证码的网络请求
     */
    private fun fetchCaptcha() {
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.getCaptcha().asResult(),
            onData = { captcha ->
                _captcha.value = captcha
            }
        )
    }

    /**
     * 执行注册操作
     */
    fun register() {
        // 验证手机号
        if (!ValidationUtil.isValidPhone(_phone.value)) {
            ToastUtils.showError(R.string.invalid_phone_number)
            return
        }

        // 验证验证码
        if (!ValidationUtil.isValidSmsCode(_verificationCode.value)) {
            ToastUtils.showError(R.string.invalid_verification_code)
            return
        }

        // 验证密码
        if (!ValidationUtil.isValidPassword(_password.value)) {
            ToastUtils.showError(R.string.invalid_password)
        }

        // 验证确认密码
        if (_password.value != _confirmPassword.value) {
            ToastUtils.showError(R.string.password_mismatch)
            return
        }

        val params = mapOf(
            "phone" to phone.value,
            "smsCode" to verificationCode.value,
            "password" to password.value,
            "confirmPassword" to confirmPassword.value
        )

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.register(params).asResult(),
            onData = { authData -> registerSuccess(authData) }
        )
    }

    /**
     * 注册成功
     */
    private fun registerSuccess(authData: Auth) {
        viewModelScope.launch {
            ToastUtils.showSuccess(R.string.register_success)
            appState.updateAuth(authData)
            appState.refreshUserInfo()
            navigateBack()
        }
    }
}
