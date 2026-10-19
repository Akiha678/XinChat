package com.seanchen.xinchat.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.navigation.NavigationOptions
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.core.navigation.navigate
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.core.util.storage.MMKVUtils
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.state.SmsLoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
        private const val KEY_SAVED_EMAIL = "saved_email"
        private const val COUNTDOWN_SECONDS = 60
    }

    private val _uiState = MutableStateFlow(SmsLoginUiState())
    val uiState: StateFlow<SmsLoginUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        loadSavedEmail()
    }

    /**
     * 更新邮箱输入
     */
    fun updateEmail(input: String) {
        _uiState.update { it.copy(email = input.trim()) }
    }

    /**
     * 兼容手机号更新别名
     */
    fun updatePhone(input: String) {
        updateEmail(input)
    }

    /**
     * 初始化邮箱（当外部传入且本地未输入时回填）
     */
    fun initEmail(email: String) {
        if (_uiState.value.email.isEmpty() && email.isNotEmpty()) {
            updateEmail(email)
        }
    }

    /**
     * 兼容手机号初始化别名
     */
    fun initPhone(phone: String) {
        initEmail(phone)
    }

    /**
     * 更新验证码输入，限制最多 4 位纯数字
     */
    fun updateVerificationCode(input: String) {
        val digitsOnly = input.filter { it.isDigit() }.take(4)
        _uiState.update { it.copy(verificationCode = digitsOnly) }
    }

    /**
     * 发送验证码
     */
    fun sendVerificationCode(onSuccess: (() -> Unit)? = null) {
        val currentEmail = _uiState.value.email
        if (!ValidationUtil.isValidEmail(currentEmail)) {
            runCatching { ToastUtils.showError(R.string.invalid_email) }
            return
        }

        if (!_uiState.value.canSendCode) {
            return
        }

        _uiState.update { it.copy(isSendingCode = true) }

        val params = mapOf(
            "email" to currentEmail,
            "phone" to currentEmail,
            "account" to currentEmail
        )
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.getSmsCode(params).asResult(),
            onData = { code ->
                if (ValidationUtil.isValidSmsCode(code)) {
                    _uiState.update { it.copy(verificationCode = code) }
                }
                runCatching { ToastUtils.showSuccess("【XinChat】验证码：$code") }
                _uiState.update { it.copy(resendCountdown = COUNTDOWN_SECONDS) }
                startCountdown()
                onSuccess?.invoke()
            },
            onFinally = {
                _uiState.update { it.copy(isSendingCode = false) }
            }
        )
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
     * 执行邮箱验证码登录
     */
    fun login(onSuccess: (() -> Unit)? = null) {
        val currentEmail = _uiState.value.email
        val currentCode = _uiState.value.verificationCode

        if (!ValidationUtil.isValidEmail(currentEmail)) {
            runCatching { ToastUtils.showError(R.string.invalid_email) }
            return
        }

        if (!ValidationUtil.isValidSmsCode(currentCode)) {
            runCatching { ToastUtils.showError(R.string.invalid_verification_code) }
            return
        }

        _uiState.update { it.copy(isLoggingIn = true) }

        val params = mapOf(
            "email" to currentEmail,
            "phone" to currentEmail,
            "account" to currentEmail,
            "code" to currentCode
        )

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.loginByPhone(params).asResult(),
            onData = { authData ->
                loginSuccess(currentEmail, authData)
                onSuccess?.invoke()
            },
            onFinally = {
                _uiState.update { it.copy(isLoggingIn = false) }
            }
        )
    }

    private fun loginSuccess(email: String, authData: Auth) {
        viewModelScope.launch {
            saveEmail(email)
            runCatching { ToastUtils.showSuccess(R.string.login_success) }
            appState.updateAuth(authData)
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

    private fun loadSavedEmail() {
        runCatching {
            MMKVUtils.getString(KEY_SAVED_EMAIL, "")
        }.getOrNull()?.let { savedEmail ->
            if (savedEmail.isNotBlank()) {
                _uiState.update { it.copy(email = savedEmail) }
            }
        }
    }

    private fun saveEmail(email: String) {
        runCatching {
            MMKVUtils.putString(KEY_SAVED_EMAIL, email)
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}