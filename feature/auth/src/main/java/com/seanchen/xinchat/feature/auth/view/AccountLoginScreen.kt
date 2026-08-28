package com.seanchen.xinchat.feature.auth.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.seanchen.xinchat.feature.auth.viewmodel.AccountLoginViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.seanchen.xinchat.core.designsystem.component.BottomNavigationRow
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXLarge
import com.seanchen.xinchat.core.navigation.auth.AuthNavigator
import com.seanchen.xinchat.core.navigation.common.CommonNavigator
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.button.AppButton
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.AnimatedAuthPage
import com.seanchen.xinchat.feature.auth.component.AuthInputField
import com.seanchen.xinchat.feature.auth.component.PasswordInputField
import com.seanchen.xinchat.feature.auth.component.UserAgreement


/**
 * 账号密码登录
 */
@Composable
internal fun AccountLoginRoute(
    viewModel: AccountLoginViewModel = hiltViewModel()
){

    // 收集账号输入
    val account by viewModel.account.collectAsState()
    // 收集密码输入
    val password by viewModel.password.collectAsState()
    // 收集登录按钮启用状态
    val isLoginEnabled by viewModel.isLoginEnabled.collectAsState(initial = false)

    AccountLoginScreen(
        account = account,
        password = password,
        isLoginEnabled = isLoginEnabled,
        onAccountChange = viewModel::updateAccount,
        onPasswordChange = viewModel::updatePassword,
        onLoginClick = viewModel::login
    )
}

@Composable
internal fun AccountLoginScreen(
    account: String = "",
    password: String = "",
    isLoginEnabled: Boolean = false,
    onAccountChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {}
){
    AnimatedAuthPage(
        title = "欢迎登录XinChat",
        onBackClick = { navigateBack() }
    ) {
        AccountLoginContentView(
            account = account,
            password = password,
            isLoginEnabled = isLoginEnabled,
            onAccountChange = onAccountChange,
            onPasswordChange = onPasswordChange,
            onLoginClick = onLoginClick
        )
    }
}

@Composable
private fun AccountLoginContentView(
    account: String,
    password: String,
    isLoginEnabled: Boolean,
    onAccountChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
){
    val accountFieldFocused = remember { mutableStateOf(false) }
    val passwordFieldFocused = remember { mutableStateOf(false) }

    AuthInputField(
        value = account,
        onValueChange = onAccountChange,
        fieldFocused = accountFieldFocused,
        placeholder = stringResource(id = R.string.account_hint),
        keyboardType = KeyboardType.Text,
        nextAction = ImeAction.Next
    )

    Spacer(modifier = Modifier.height(42.dp))

    PasswordInputField(
        password = password,
        onPasswordChange = onPasswordChange,
        passwordFieldFocused = passwordFieldFocused,
        placeholder = stringResource(id = R.string.password_hint),
        nextAction = ImeAction.Done
    )

    SpaceVerticalMedium()

    UserAgreement(
        prefix = stringResource(id = R.string.login_agreement_prefix),
        onUserAgreementClick = CommonNavigator::toUserAgreement,
        onPrivacyPolicyClick = CommonNavigator::toPrivacyPolicy
    )

    SpaceVerticalXLarge()

    AppButton(
        text = stringResource(id = R.string.login),
        onClick = onLoginClick,
        enabled = isLoginEnabled
    )

    BottomNavigationRow(
        messageText = stringResource(id = R.string.go_register),
        actionText = stringResource(id = R.string.forgot_password),
        onCancelClick = { AuthNavigator.toRegister() },
        onActionClick = { AuthNavigator.toResetPassword() },
        divider = true
    )
}
