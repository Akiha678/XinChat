package com.seanchen.xinchat.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.navigation.NavigationOptions
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.core.navigation.navigate
import com.seanchen.xinchat.core.util.storage.MMKVUtils
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.state.SmsLoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SmsLoginViewModel @Inject constructor(
    private val appState: AppState,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    companion object {
        private const val KEY_SAVED_PHONE = "saved_phone"
        private const val COUNTDOWN_SECONDS = 60
    }

    private val _uiState = MutableStateFlow(SmsLoginUiState())
    val uiState: StateFlow<SmsLoginUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        loadSavedPhone()
    }

    /**
     * 更新手机号输入，限制最多 11 位且纯数字
     */
    fun updatePhone(input: String) {
        val digitsOnly = input.filter { it.isDigit() }.take(11)
        _uiState.update { it.copy(phone = digitsOnly) }
    }

    /**
     * 更新验证码输入，限制最多 4 位且纯数字
     */
    fun updateVerificationCode(input: String) {
        val digitsOnly = input.filter { it.isDigit() }.take(4)
        _uiState.update { it.copy(verificationCode = digitsOnly) }
    }

    /**
     * 发送短信验证码（UI 层模拟实现）
     */
    fun sendVerificationCode() {
        val currentPhone = _uiState.value.phone
        if (!ValidationUtil.isValidPhone(currentPhone)) {
            runCatching { ToastUtils.showError(R.string.invalid_phone_number) }
            return
        }

        if (!_uiState.value.canSendCode) {
            return
        }

        _uiState.update { it.copy(isSendingCode = true) }

        viewModelScope.launch {
            // 模拟短暂网络延迟
            delay(300)

            // 模拟生成 4 位随机验证码并自动填入
            val code = String.format("%04d", Random.nextInt(1000, 10000))
            runCatching { ToastUtils.showSuccess("【XinChat】模拟验证码：$code") }

            _uiState.update {
                it.copy(
                    isSendingCode = false,
                    verificationCode = code,
                    resendCountdown = COUNTDOWN_SECONDS
                )
            }

            startCountdown()
        }
    }

    /**
     * 启动重新发送倒计时
     */
    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_uiState.value.resendCountdown > 0) {
                delay(1000L)
                _uiState.update {
                    val nextCountdown = (it.resendCountdown - 1).coerceAtLeast(0)
                    it.copy(resendCountdown = nextCountdown)
                }
            }
        }
    }

    /**
     * 执行短信验证码登录
     */
    fun login() {
        val currentPhone = _uiState.value.phone
        val currentCode = _uiState.value.verificationCode

        if (!ValidationUtil.isValidPhone(currentPhone)) {
            runCatching { ToastUtils.showError(R.string.invalid_phone_number) }
            return
        }

        if (!ValidationUtil.isValidSmsCode(currentCode)) {
            runCatching { ToastUtils.showError(R.string.invalid_verification_code) }
            return
        }

        _uiState.update { it.copy(isLoggingIn = true) }

        viewModelScope.launch {
            try {
                // 短信登录在后端暂不可用，UI 层模拟完成登录态构建
                delay(400)
                val mockAuth = Auth(
                    token = "sms_mock_token_${System.currentTimeMillis()}",
                    refreshToken = "sms_mock_refresh_token",
                    expire = 7 * 24 * 3600L,
                    refreshExpire = 30 * 24 * 3600L,
                    createdAt = System.currentTimeMillis()
                )

                val mockUser = User(
                    id = currentPhone.takeLast(6).toLongOrNull() ?: 10001L,
                    unionid = currentPhone,
                    nickName = "用户_${currentPhone.takeLast(4)}",
                    phone = currentPhone,
                    avatarUrl = null,
                    gender = 0,
                    status = 1,
                    loginType = "1"
                )

                appState.updateUserState(mockAuth, mockUser)
                savePhone(currentPhone)

                runCatching { ToastUtils.showSuccess(R.string.login_success) }

                navigate(
                    route = MainRoutes.Main,
                    navOptions = NavigationOptions(
                        popUpToRoute = AuthRoutes.Login,
                        inclusive = true,
                        allowPopToEmpty = true
                    )
                )
            } catch (e: Exception) {
                runCatching { ToastUtils.showError(e.message ?: "登录失败，请重试") }
            } finally {
                _uiState.update { it.copy(isLoggingIn = false) }
            }
        }
    }

    private fun loadSavedPhone() {
        runCatching {
            MMKVUtils.getString(KEY_SAVED_PHONE, "")
        }.getOrNull()?.let { savedPhone ->
            if (savedPhone.isNotBlank()) {
                _uiState.update { it.copy(phone = savedPhone) }
            }
        }
    }

    private fun savePhone(phone: String) {
        runCatching {
            MMKVUtils.putString(KEY_SAVED_PHONE, phone)
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}