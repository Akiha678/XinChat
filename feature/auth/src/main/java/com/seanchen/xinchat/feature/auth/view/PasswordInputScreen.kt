package com.seanchen.xinchat.feature.auth.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.ModernAuthButton
import com.seanchen.xinchat.feature.auth.component.ModernAuthScaffold
import com.seanchen.xinchat.feature.auth.component.ModernPasswordInput
import com.seanchen.xinchat.feature.auth.viewmodel.AccountLoginViewModel

/**
 * 邮箱密码登录 - 第二步（输入密码界面）
 */
@Composable
fun PasswordInputRoute(
    account: String,
    viewModel: AccountLoginViewModel = run {
        val activity = LocalContext.current as? ComponentActivity
        if (activity != null) hiltViewModel(activity) else hiltViewModel()
    }
) {
    LaunchedEffect(account) {
        viewModel.initAccount(account)
    }

    val password by viewModel.password.collectAsStateWithLifecycle()
    val isLoginEnabled by viewModel.isLoginEnabled.collectAsStateWithLifecycle(initialValue = false)
    val isLoggingIn by viewModel.isLoggingIn.collectAsStateWithLifecycle()

    PasswordInputScreen(
        account = account,
        password = password,
        isLoginEnabled = isLoginEnabled,
        isLoggingIn = isLoggingIn,
        onPasswordChange = viewModel::updatePassword,
        onLoginClick = { viewModel.login() },
        onBackClick = { navigateBack() }
    )
}

@Composable
fun PasswordInputScreen(
    account: String,
    password: String = "",
    isLoginEnabled: Boolean = false,
    isLoggingIn: Boolean = false,
    onPasswordChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val subtitleText = stringResource(id = R.string.account_label, account)

    ModernAuthScaffold(
        title = stringResource(id = R.string.input_password_title),
        subtitle = subtitleText,
        isCloseAction = false,
        onNavigationClick = onBackClick
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 密码输入框
        ModernPasswordInput(
            password = password,
            onPasswordChange = onPasswordChange,
            placeholder = stringResource(id = R.string.password_hint),
            onDone = {
                if (isLoginEnabled && !isLoggingIn) {
                    onLoginClick()
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 登录主按钮
        ModernAuthButton(
            text = if (isLoggingIn) "登录中..." else stringResource(id = R.string.login),
            enabled = isLoginEnabled,
            isLoading = isLoggingIn,
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(name = "密码输入页", showBackground = true)
@Composable
private fun PasswordInputScreenPreview() {
    PasswordInputScreen(
        account = "test@example.com",
        password = "password123",
        isLoginEnabled = true
    )
}
