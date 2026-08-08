package com.seanchen.xinchat.feature.auth.viewmodel

import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class AccountLoginViewModel @Inject constructor(
//    private val appState: AppState,
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    companion object {
        private const val KEY_SAVED_PHONE = "saved_phone"

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
        // 加载已保存的账号和密码
        loadSavedCredentials()
    }

    /**
     * 登录按钮是否可用
     */
    val isLoginEnabled = _account.combine(_password) { account, password ->
        ValidationUtil.isValidPhone(account) && ValidationUtil.isValidPassword(password)
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
        // 验证手机号
        if (!ValidationUtil.isValidPhone(_account.value)) {
            ToastUtils.showError(R.string.invalid_phone_number)
            return
        }

        // 验证密码
        if (!ValidationUtil.isValidPassword(_password.value)) {
            ToastUtils.showError(R.string.invalid_password)
            return
        }

        val params = mapOf(
            "phone" to account.value,
            "password" to password.value
        )

        ResultHandler.handleResultWithData(

        )
    }

    /**
     * 加载已经保存的凭据
     */
    private fun loadSavedCredentials(){

    }

    /**
     * 保存凭据
     */
    private fun saveCredentials(phone: String, password: String) {

    }
}