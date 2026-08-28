package com.seanchen.xinchat.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.navigation.NavigationOptions
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.core.navigation.navigate
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.asResult
import com.seanchen.xinchat.core.util.storage.MMKVUtils
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountLoginViewModel @Inject constructor(
    private val appState: AppState,
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    companion object {
        private const val KEY_SAVED_ACCOUNT = "saved_account"

        private const val KEY_SAVED_PASSWORD = "saved_password"
    }

    /**
     * 账号输入
     */
    private val _account = MutableStateFlow("")
    val account: StateFlow<String> = _account

    /**
     * 密码输入
     */
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    init {
        // 只回填账号，历史版本保存过明文密码时在这里清理。
        loadSavedAccount()
    }

    /**
     * 登录按钮是否可用
     */
    val isLoginEnabled = _account.combine(_password) { account, password ->
        account.isNotBlank() && password.length >= 6
    }

    /**
     * 更新账号输入
     */
    fun updateAccount(value: String) {
        _account.value = value
    }

    /**
     * 更新密码输入
     */
    fun updatePassword(value: String) {
        _password.value = value
    }

    /**
     * 执行登录操作
     */
    fun login() {
        // 验证账号
        if (_account.value.isBlank()) {
            ToastUtils.showError(R.string.invalid_account)
            return
        }

        // 验证密码
        if (!ValidationUtil.isValidPassword(_password.value)) {
            ToastUtils.showError(R.string.invalid_password)
            return
        }

        val params = mapOf(
            "account" to account.value,
            "password" to password.value
        )

        ResultHandler.handleResultWithData(
            scope = viewModelScope,
            flow = authRepository.loginByPassword(params).asResult(),
            onData = { authData -> loginSuccess(authData) }
        )
    }

    /**
     * 登录成功
     */
    private fun loginSuccess(authData: Auth) {
        viewModelScope.launch {
            saveAccount(_account.value)
            ToastUtils.showSuccess(R.string.login_success)
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

    /**
     * 加载已经保存的账号
     */
    private fun loadSavedAccount() {
        val savedAccount = MMKVUtils.getString(KEY_SAVED_ACCOUNT, "")

        if (savedAccount.isNotEmpty()){
            _account.value = savedAccount
        }

        MMKVUtils.putString(KEY_SAVED_PASSWORD, "")
    }

    /**
     * 保存账号
     */
    private fun saveAccount(account: String) {
        MMKVUtils.putString(KEY_SAVED_ACCOUNT, account)
    }
}
