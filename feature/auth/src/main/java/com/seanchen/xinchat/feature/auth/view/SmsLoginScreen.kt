package com.seanchen.xinchat.feature.auth.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.MaterialTheme
import com.seanchen.xinchat.core.designsystem.component.BottomNavigationRow
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXLarge
import com.seanchen.xinchat.core.navigation.auth.AuthNavigator
import com.seanchen.xinchat.core.navigation.common.CommonNavigator
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.widget.ui.button.AppButton
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.AnimatedAuthPage
import com.seanchen.xinchat.feature.auth.component.PhoneInputField
import com.seanchen.xinchat.feature.auth.component.UserAgreement
import com.seanchen.xinchat.feature.auth.component.VerificationCodeField
import com.seanchen.xinchat.feature.auth.state.SmsLoginUiState
import com.seanchen.xinchat.feature.auth.viewmodel.SmsLoginViewModel

@Composable
fun SmsLoginRoute(
    viewModel: SmsLoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SmsLoginScreen(
        uiState = uiState,
        onPhoneChange = viewModel::updatePhone,
        onVerificationCodeChange = viewModel::updateVerificationCode,
        onSendVerificationCode = viewModel::sendVerificationCode,
        onLoginClick = viewModel::login,
        onBackClick = { navigateBack() },
        onToAccountLoginClick = { AuthNavigator.toAccountLogin() },
        onToRegisterClick = { AuthNavigator.toRegister() }
    )
}

@Composable
fun SmsLoginScreen(
    uiState: SmsLoginUiState = SmsLoginUiState(),
    onPhoneChange: (String) -> Unit = {},
    onVerificationCodeChange: (String) -> Unit = {},
    onSendVerificationCode: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onToAccountLoginClick: () -> Unit = {},
    onToRegisterClick: () -> Unit = {}
) {
    AnimatedAuthPage(
        title = stringResource(id = R.string.sms_login),
        onBackClick = onBackClick
    ) {
        SmsLoginContentView(
            uiState = uiState,
            onPhoneChange = onPhoneChange,
            onVerificationCodeChange = onVerificationCodeChange,
            onSendVerificationCode = onSendVerificationCode,
            onLoginClick = onLoginClick,
            onToAccountLoginClick = onToAccountLoginClick,
            onToRegisterClick = onToRegisterClick
        )
    }
}

@Composable
private fun SmsLoginContentView(
    uiState: SmsLoginUiState,
    onPhoneChange: (String) -> Unit,
    onVerificationCodeChange: (String) -> Unit,
    onSendVerificationCode: () -> Unit,
    onLoginClick: () -> Unit,
    onToAccountLoginClick: () -> Unit,
    onToRegisterClick: () -> Unit
) {
    val phoneFieldFocused = remember { mutableStateOf(false) }
    val codeFieldFocused = remember { mutableStateOf(false) }

    PhoneInputField(
        phone = uiState.phone,
        onPhoneChange = onPhoneChange,
        phoneFieldFocused = phoneFieldFocused,
        placeholder = stringResource(id = R.string.phone_hint),
        nextAction = ImeAction.Next
    )

    Spacer(modifier = Modifier.height(42.dp))

    val sendButtonText = when {
        uiState.isSendingCode -> "发送中..."
        uiState.resendCountdown > 0 -> "重新发送 (${uiState.resendCountdown}s)"
        else -> stringResource(id = R.string.get_verification_code)
    }

    VerificationCodeField(
        verificationCode = uiState.verificationCode,
        onVerificationCodeChange = onVerificationCodeChange,
        codeFieldFocused = codeFieldFocused,
        onSendVerificationCode = onSendVerificationCode,
        placeholder = stringResource(id = R.string.verification_code),
        nextAction = ImeAction.Done,
        isEnabled = uiState.canSendCode,
        buttonText = sendButtonText
    )

    SpaceVerticalMedium()

    UserAgreement(
        prefix = stringResource(id = R.string.login_agreement_prefix),
        onUserAgreementClick = CommonNavigator::toUserAgreement,
        onPrivacyPolicyClick = CommonNavigator::toPrivacyPolicy
    )

    SpaceVerticalXLarge()

    AppButton(
        text = if (uiState.isLoggingIn) "登录中..." else stringResource(id = R.string.login),
        onClick = onLoginClick,
        enabled = uiState.canLogin
    )

    BottomNavigationRow(
        messageText = stringResource(id = R.string.go_register),
        actionText = stringResource(id = R.string.account_login),
        onCancelClick = onToRegisterClick,
        onActionClick = onToAccountLoginClick,
        divider = true
    )
}

@Preview(name = "默认空状态", showBackground = true)
@Composable
private fun SmsLoginScreenDefaultPreview() {
    MaterialTheme {
        SmsLoginScreen(
            uiState = SmsLoginUiState()
        )
    }
}

@Preview(name = "已输入状态", showBackground = true)
@Composable
private fun SmsLoginScreenFilledPreview() {
    MaterialTheme {
        SmsLoginScreen(
            uiState = SmsLoginUiState(
                phone = "13800138000",
                verificationCode = "1234"
            )
        )
    }
}

@Preview(name = "倒计时状态", showBackground = true)
@Composable
private fun SmsLoginScreenCountdownPreview() {
    MaterialTheme {
        SmsLoginScreen(
            uiState = SmsLoginUiState(
                phone = "13800138000",
                resendCountdown = 45
            )
        )
    }
}

@Preview(name = "深色模式", showBackground = true)
@Composable
private fun SmsLoginScreenDarkPreview() {
    MaterialTheme {
        SmsLoginScreen(
            uiState = SmsLoginUiState(
                phone = "13800138000",
                verificationCode = "6688"
            )
        )
    }
}