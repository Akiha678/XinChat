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
import com.seanchen.xinchat.feature.auth.state.LoginMode
import com.seanchen.xinchat.feature.auth.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 统一登录 ViewModel（仅包含邮箱验证码登录与邮箱密码登录）
 */
@HiltViewModel
open class LoginViewModel @Inject constructor(
    private val appState: AppState,
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    companion object {
        private const val KEY_SAVED_EMAIL = "saved_email"
        private const val COUNTDOWN_SECONDS = 60
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        loadSavedEmail()
    }

    // ==================================================================================
    // 输入项更新与模式切换
    // ==================================================================================

    /**
     * 更新邮箱输入
     */
    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value.trim()) }
    }

    /**
     * 初始化邮箱（可选择是否自动开始重新获取倒计时）
     */
    fun initEmail(email: String, autoStartCountdown: Boolean = false) {
        if (email.isNotEmpty()) {
            if (_uiState.value.email.isEmpty() || _uiState.value.email != email) {
                _uiState.update { it.copy(email = email.trim()) }
            }
            if (autoStartCountdown && _uiState.value.resendCountdown == 0) {
                _uiState.update { it.copy(resendCountdown = COUNTDOWN_SECONDS) }
                startCountdown()
            }
        }
    }

    /**
     * 更新密码输入
     */
    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    /**
     * 更新验证码输入（限制最多 4 位纯数字）
     */
    fun updateVerificationCode(input: String) {
        val digitsOnly = input.filter { it.isDigit() }.take(4)
        _uiState.update { it.copy(verificationCode = digitsOnly) }
    }

    /**
     * 更新用户协议勾选状态
     */
    fun updateAgreed(agreed: Boolean) {
        _uiState.update { it.copy(isAgreed = agreed) }
    }

    /**
     * 切换登录方式（验证码登录 / 密码登录）
     */
    fun switchLoginMode(mode: LoginMode) {
        if (_uiState.value.loginMode != mode) {
            _uiState.update { it.copy(loginMode = mode) }
        }
    }

    /**
     * 在验证码登录与密码登录之间互切（保持已输入的邮箱与勾选状态）
     */
    fun toggleLoginMode() {
        _uiState.update {
            it.copy(
                loginMode = if (it.loginMode == LoginMode.CODE) LoginMode.PASSWORD else LoginMode.CODE
            )
        }
    }

    // ==================================================================================
    // 验证码操作
    // ==================================================================================

    /**
     * 发送邮箱验证码
     *
     * @param requireAgreement 是否需要强制先勾选用户协议
     * @param onSuccess 发送成功回调
     */
    fun sendVerificationCode(
        requireAgreement: Boolean = false,
        onSuccess: (() -> Unit)? = null
    ) {
        if (requireAgreement && !_uiState.value.isAgreed) {
            runCatching { ToastUtils.showError(R.string.please_agree_agreement) }
            return
        }

        val currentEmail = _uiState.value.email
        if (!ValidationUtil.isValidEmail(currentEmail)) {
            runCatching { ToastUtils.showError(R.string.invalid_email) }
            return
        }

        if (!_uiState.value.canSendCode) {
            return
        }

        _uiState.update { it.copy(isSendingCode = true) }

        val params = mapOf("email" to currentEmail)
        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.getEmailCode(params).asResult(),
            onData = { codeOrMsg ->
                if (ValidationUtil.isValidCode(codeOrMsg)) {
                    _uiState.update { it.copy(verificationCode = codeOrMsg) }
                    runCatching { ToastUtils.showSuccess("【XinChat】验证码：$codeOrMsg") }
                } else {
                    runCatching { ToastUtils.showSuccess(codeOrMsg.ifEmpty { "验证码已发送" }) }
                }
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

    // ==================================================================================
    // 登录操作
    // ==================================================================================

    /**
     * 执行邮箱验证码登录（未注册时服务端自动完成注册创建账号）
     */
    fun loginByCode(onSuccess: (() -> Unit)? = null) {
        val currentEmail = _uiState.value.email
        val currentCode = _uiState.value.verificationCode

        if (!ValidationUtil.isValidEmail(currentEmail)) {
            runCatching { ToastUtils.showError(R.string.invalid_email) }
            return
        }

        if (!ValidationUtil.isValidCode(currentCode)) {
            runCatching { ToastUtils.showError(R.string.invalid_verification_code) }
            return
        }

        _uiState.update { it.copy(isLoggingIn = true) }

        val params = mapOf(
            "email" to currentEmail,
            "code" to currentCode
        )

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.loginByEmail(params).asResult(),
            onData = { authData ->
                loginSuccess(currentEmail, authData)
                onSuccess?.invoke()
            },
            onFinally = {
                _uiState.update { it.copy(isLoggingIn = false) }
            }
        )
    }

    /**
     * 执行邮箱密码登录
     */
    fun loginByPassword(onSuccess: (() -> Unit)? = null) {
        val currentEmail = _uiState.value.email.trim()
        val currentPassword = _uiState.value.password

        if (!ValidationUtil.isValidEmail(currentEmail)) {
            runCatching { ToastUtils.showError(R.string.invalid_email) }
            return
        }

        if (!ValidationUtil.isValidPassword(currentPassword)) {
            runCatching { ToastUtils.showError(R.string.invalid_password) }
            return
        }

        _uiState.update { it.copy(isLoggingIn = true) }

        val params = mapOf(
            "email" to currentEmail,
            "password" to currentPassword
        )

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.loginByPassword(params).asResult(),
            onData = { authData ->
                loginSuccess(currentEmail, authData)
                onSuccess?.invoke()
            },
            onFinally = {
                _uiState.update { it.copy(isLoggingIn = false) }
            }
        )
    }

    /**
     * 通用登录方法（若填入了验证码则走验证码登录，否则走密码登录）
     */
    fun login(onSuccess: (() -> Unit)? = null) {
        if (_uiState.value.verificationCode.isNotEmpty()) {
            loginByCode(onSuccess)
        } else {
            loginByPassword(onSuccess)
        }
    }

    /**
     * 登录成功后续处理
     */
    private fun loginSuccess(email: String, authData: Auth) {
        viewModelScope.launch {
            saveEmail(email)
            runCatching { ToastUtils.showSuccess(R.string.login_success) }
            appState.updateAuth(authData)
            appState.refreshUserInfo()
            navigate(
                route = MainRoutes.Main,
                navOptions = NavigationOptions(
                    popUpToRoute = AuthRoutes.CodeLogin,
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
