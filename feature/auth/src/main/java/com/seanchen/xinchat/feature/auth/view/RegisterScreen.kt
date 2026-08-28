package com.seanchen.xinchat.feature.auth.view


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.seanchen.xinchat.core.designsystem.component.BottomNavigationRow
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXLarge
import com.seanchen.xinchat.core.navigation.common.CommonNavigator
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.button.AppButton
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.AnimatedAuthPage
import com.seanchen.xinchat.feature.auth.component.AuthInputField
import com.seanchen.xinchat.feature.auth.component.PasswordInputField
import com.seanchen.xinchat.feature.auth.component.UserAgreement
import com.seanchen.xinchat.feature.auth.component.VerificationCodeField
import com.seanchen.xinchat.feature.auth.viewmodel.RegisterViewModel

@Composable
internal fun RegisterRoute(
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    // 收集邮箱输入
    val email by viewModel.email.collectAsState()
    // 收集验证码输入
    val verificationCode by viewModel.verificationCode.collectAsState()
    // 收集密码输入
    val password by viewModel.password.collectAsState()
    // 收集确认密码输入
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    // 收集验证码加载状态
    val isLoadingCaptcha by viewModel.isLoadingCaptcha.collectAsState()
    // 收集邮箱验证状态
    val isEmailValid by viewModel.isEmailValid.collectAsState(initial = false)
    // 收集注册按钮启动状态
    val isRegisterEnabled by viewModel.isRegisterEnabled.collectAsState(initial = false)

    RegisterScreen(
        email = email,
        verificationCode = verificationCode,
        password = password,
        confirmPassword = confirmPassword,
        isLoadingCaptcha = isLoadingCaptcha,
        isEmailValid = isEmailValid,
        isRegisterEnabled = isRegisterEnabled,
        onEmailChange = viewModel::updateEmail,
        onVerificationCodeChange = viewModel::updateVerificationCode,
        onPasswordChange = viewModel::updatePassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onSendVerificationCode = viewModel::onSendCodeButtonClick,
        onRegisterClick = viewModel::register
    )
}

//@OptIn(ExperimentalMaterial3Api)
@Composable
internal fun RegisterScreen(
    email: String = "",
    verificationCode: String = "",
    password: String = "",
    confirmPassword: String = "",
    isLoadingCaptcha: Boolean = false,
    isEmailValid: Boolean = false,
    isRegisterEnabled: Boolean = false,
    onEmailChange: (String) -> Unit = {},
    onVerificationCodeChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSendVerificationCode: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    AnimatedAuthPage(
        title = stringResource(id = R.string.welcome_register),
        withFadeIn = true,
        onBackClick = { navigateBack() }
    ) {
        RegisterContentView(
            email = email,
            verificationCode = verificationCode,
            password = password,
            confirmPassword = confirmPassword,
            isEmailValid = isEmailValid,
            isRegisterEnabled = isRegisterEnabled,
            isLoadingCaptcha = isLoadingCaptcha,
            onEmailChange = onEmailChange,
            onVerificationCodeChange = onVerificationCodeChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onSendVerificationCode = onSendVerificationCode,
            onRegisterClick = onRegisterClick
        )
    }
}


@Composable
private fun RegisterContentView(
    email: String,
    verificationCode: String,
    password: String,
    confirmPassword: String,
    isEmailValid: Boolean,
    isRegisterEnabled: Boolean,
    isLoadingCaptcha: Boolean,
    onEmailChange: (String) -> Unit,
    onVerificationCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSendVerificationCode: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val emailFieldFocused = remember { mutableStateOf(false) }
    val codeFieldFocused = remember { mutableStateOf(false) }
    val passwordFieldFocused = remember { mutableStateOf(false) }
    val confirmPasswordFieldFocused = remember { mutableStateOf(false) }

    AuthInputField(
        value = email,
        onValueChange = onEmailChange,
        fieldFocused = emailFieldFocused,
        placeholder = stringResource(id = R.string.email_hint),
        keyboardType = KeyboardType.Email,
        nextAction = ImeAction.Next
    )

    Spacer(modifier = Modifier.height(30.dp))

    VerificationCodeField(
        verificationCode = verificationCode,
        onVerificationCodeChange = onVerificationCodeChange,
        codeFieldFocused = codeFieldFocused,
        onSendVerificationCode = onSendVerificationCode,
        placeholder = stringResource(id = R.string.verification_code),
        nextAction = ImeAction.Next,
        isEnabled = isEmailValid && !isLoadingCaptcha
    )

    Spacer(modifier = Modifier.height(30.dp))

    PasswordInputField(
        password = password,
        onPasswordChange = onPasswordChange,
        passwordFieldFocused = passwordFieldFocused,
        placeholder = stringResource(id = R.string.set_password),
        nextAction = ImeAction.Next
    )

    Spacer(modifier = Modifier.height(30.dp))

    PasswordInputField(
        password = confirmPassword,
        onPasswordChange = onConfirmPasswordChange,
        passwordFieldFocused = confirmPasswordFieldFocused,
        placeholder = stringResource(id = R.string.confirm_password),
        nextAction = ImeAction.Done
    )

    SpaceVerticalMedium()

    UserAgreement(
        prefix = stringResource(id = R.string.register_agreement_prefix),
        onUserAgreementClick = CommonNavigator::toUserAgreement,
        onPrivacyPolicyClick = CommonNavigator::toPrivacyPolicy
    )

    SpaceVerticalXLarge()

    AppButton(
        text = stringResource(id = R.string.register),
        onClick = onRegisterClick,
        enabled = isRegisterEnabled
    )

    BottomNavigationRow(
        messageText = stringResource(id = R.string.have_account),
        actionText = stringResource(id = R.string.go_login),
        onActionClick = { navigateBack() }
    )
}
