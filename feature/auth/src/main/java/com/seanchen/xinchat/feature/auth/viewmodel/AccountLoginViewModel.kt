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
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class AccountLoginViewModel @Inject constructor(
    private val appState: AppState,
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    /**
     * 邮箱/账号输入
     */
    private val _account = MutableStateFlow("")
    val account: StateFlow<String> = _account

    /**
     * 密码输入
     */
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    /**
     * 是否正在登录
     */
    private val _isLoggingIn = MutableStateFlow(false)
    val isLoggingIn: StateFlow<Boolean> = _isLoggingIn

    /**
     * 登录按钮是否可用（邮箱有效且密码不少于6位）
     */
    val isLoginEnabled = _account.combine(_password) { account, password ->
        ValidationUtil.isValidEmail(account.trim()) && password.length >= 6
    }

    /**
     * 初始化账号
     */
    fun initAccount(value: String) {
        if (_account.value.isEmpty() && value.isNotEmpty()) {
            _account.value = value.trim()
        }
    }

    /**
     * 更新账号输入
     */
    fun updateAccount(value: String) {
        _account.value = value.trim()
    }

    /**
     * 更新密码输入
     */
    fun updatePassword(value: String) {
        _password.value = value
    }

    /**
     * 执行邮箱密码登录
     */
    fun login(onSuccess: (() -> Unit)? = null) {
        val email = _account.value.trim()

        // 验证邮箱
        if (!ValidationUtil.isValidEmail(email)) {
            runCatching { ToastUtils.showError(R.string.invalid_email) }
            return
        }

        // 验证密码
        if (!ValidationUtil.isValidPassword(_password.value)) {
            runCatching { ToastUtils.showError(R.string.invalid_password) }
            return
        }

        _isLoggingIn.value = true
        val params = mapOf(
            "account" to email,
            "email" to email,
            "password" to password.value
        )

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.loginByPassword(params).asResult(),
            onData = { authData -> 
                loginSuccess(authData)
                onSuccess?.invoke()
            },
            onFinally = {
                _isLoggingIn.value = false
            }
        )
    }

    /**
     * 登录成功
     */
    private fun loginSuccess(authData: Auth) {
        viewModelScope.launch {
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
}
