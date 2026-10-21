package com.seanchen.xinchat.feature.auth.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.designsystem.theme.PrimaryDefault
import com.seanchen.xinchat.core.navigation.auth.AuthNavigator
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.ImageCaptchaDialog
import com.seanchen.xinchat.feature.auth.component.ModernAuthAgreement
import com.seanchen.xinchat.feature.auth.component.ModernAuthBranding
import com.seanchen.xinchat.feature.auth.component.ModernAuthButton
import com.seanchen.xinchat.feature.auth.component.ModernAuthScaffold
import com.seanchen.xinchat.feature.auth.component.ModernCodeBoxes
import com.seanchen.xinchat.feature.auth.component.ModernEmailInput
import com.seanchen.xinchat.feature.auth.component.ModernPasswordInput
import com.seanchen.xinchat.feature.auth.state.LoginMode
import com.seanchen.xinchat.feature.auth.state.LoginUiState
import com.seanchen.xinchat.feature.auth.viewmodel.LoginViewModel

// ======================================================================================
// 1. 登录主界面（统一承载验证码登录与密码登录，支持同页平滑切换，避免反复创建页面导致返回栈堆叠）
// ======================================================================================

/**
 * 统一登录路由
 *
 * @param initialMode 初始展示的登录方式（验证码登录 / 密码登录）
 */
@Composable
fun LoginRoute(
    initialMode: LoginMode = LoginMode.CODE,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LaunchedEffect(initialMode) {
        viewModel.switchLoginMode(initialMode)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::updateEmail,
        onAgreedChange = viewModel::updateAgreed,
        onToggleModeClick = viewModel::toggleLoginMode,
        onSendVerificationCode = {
            viewModel.openCaptchaDialog(requireAgreement = true)
        },
        onNextClick = {
            if (!uiState.isAgreed) {
                runCatching { ToastUtils.showError(R.string.please_agree_agreement) }
            } else {
                AuthNavigator.toPasswordInput(uiState.email)
            }
        }
    )

    if (uiState.showCaptchaDialog) {
        ImageCaptchaDialog(
            visible = uiState.showCaptchaDialog,
            captcha = uiState.captcha,
            isLoading = uiState.isLoadingCaptcha,
            isVerifying = uiState.isVerifyingCaptcha,
            onDismiss = viewModel::dismissCaptchaDialog,
            onRefreshCaptcha = viewModel::refreshCaptcha,
            onConfirm = { captchaCode ->
                viewModel.verifyCaptchaAndSendCode(
                    captchaCode = captchaCode,
                    onSuccess = {
                        AuthNavigator.toCodeVerify(uiState.email)
                    }
                )
            }
        )
    }
}

/**
 * 兼容旧路由入口：验证码登录
 */
@Composable
fun CodeLoginRoute(
    viewModel: LoginViewModel = hiltViewModel()
) {
    LoginRoute(initialMode = LoginMode.CODE, viewModel = viewModel)
}

/**
 * 兼容旧路由入口：密码登录
 */
@Composable
fun PasswordLoginRoute(
    viewModel: LoginViewModel = hiltViewModel()
) {
    LoginRoute(initialMode = LoginMode.PASSWORD, viewModel = viewModel)
}

/**
 * 统一登录界面
 */
@Composable
fun LoginScreen(
    uiState: LoginUiState = LoginUiState(),
    onEmailChange: (String) -> Unit = {},
    onAgreedChange: (Boolean) -> Unit = {},
    onToggleModeClick: () -> Unit = {},
    onSendVerificationCode: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {}
) {
    val isCodeMode = uiState.loginMode == LoginMode.CODE
    val pageTitle = if (isCodeMode) stringResource(id = R.string.code_login) else stringResource(id = R.string.password_login)
    val buttonText = if (isCodeMode) stringResource(id = R.string.get_verification_code) else stringResource(id = R.string.next_step)
    val switchText = if (isCodeMode) stringResource(id = R.string.password_login) else stringResource(id = R.string.code_login)

    ModernAuthScaffold(
        title = pageTitle,
        showNavigationIcon = false
    ) {
        ModernEmailInput(
            email = uiState.email,
            onEmailChange = onEmailChange,
            placeholder = stringResource(id = R.string.email_hint),
            onDone = {
                if (uiState.isEmailValid) {
                    if (isCodeMode) onSendVerificationCode() else onNextClick()
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 用户协议与隐私政策
        ModernAuthAgreement(
            isAgreed = uiState.isAgreed,
            onAgreedChange = onAgreedChange,
            onUserAgreementClick = onUserAgreementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 主操作按钮
        ModernAuthButton(
            text = buttonText,
            enabled = uiState.isEmailValid,
            isLoading = if (isCodeMode) uiState.isSendingCode else false,
            onClick = {
                if (isCodeMode) onSendVerificationCode() else onNextClick()
            }
        )

        // 切换登录方式（在当前界面无缝切换，不入导航栈，保持输入内容）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = switchText,
                color = PrimaryDefault,
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggleModeClick
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 底部品牌
        ModernAuthBranding(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

/** 兼容旧单独组件：验证码登录界面 */
@Composable
fun CodeLoginScreen(
    uiState: LoginUiState = LoginUiState(),
    onEmailChange: (String) -> Unit = {},
    onAgreedChange: (Boolean) -> Unit = {},
    onSendVerificationCode: () -> Unit = {},
    onToPasswordLoginClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {}
) {
    LoginScreen(
        uiState = uiState.copy(loginMode = LoginMode.CODE),
        onEmailChange = onEmailChange,
        onAgreedChange = onAgreedChange,
        onToggleModeClick = onToPasswordLoginClick,
        onSendVerificationCode = onSendVerificationCode,
        onCloseClick = onCloseClick,
        onUserAgreementClick = onUserAgreementClick,
        onPrivacyPolicyClick = onPrivacyPolicyClick
    )
}

/** 兼容旧单独组件：密码登录界面 */
@Composable
fun PasswordLoginScreen(
    uiState: LoginUiState = LoginUiState(),
    onEmailChange: (String) -> Unit = {},
    onAgreedChange: (Boolean) -> Unit = {},
    onNextClick: (String) -> Unit = {},
    onToCodeLoginClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {}
) {
    LoginScreen(
        uiState = uiState.copy(loginMode = LoginMode.PASSWORD),
        onEmailChange = onEmailChange,
        onAgreedChange = onAgreedChange,
        onToggleModeClick = onToCodeLoginClick,
        onNextClick = { onNextClick(uiState.email) },
        onCloseClick = onCloseClick,
        onUserAgreementClick = onUserAgreementClick,
        onPrivacyPolicyClick = onPrivacyPolicyClick
    )
}

// ======================================================================================
// 2. 验证码输入步骤（第二步：输入 4 位验证码并执行登录）
// ======================================================================================

/**
 * 验证码输入路由
 */
@Composable
fun CodeVerifyRoute(
    email: String,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LaunchedEffect(email) {
        viewModel.initEmail(email, autoStartCountdown = true)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CodeVerifyScreen(
        email = email,
        verificationCode = uiState.verificationCode,
        isLoggingIn = uiState.isLoggingIn,
        canLogin = uiState.canCodeLogin,
        resendCountdown = uiState.resendCountdown,
        onCodeChange = viewModel::updateVerificationCode,
        onSendVerificationCode = {
            viewModel.openCaptchaDialog(requireAgreement = false)
        },
        onLoginClick = { viewModel.loginByCode() },
        onBackClick = { navigateBack() }
    )

    if (uiState.showCaptchaDialog) {
        ImageCaptchaDialog(
            visible = uiState.showCaptchaDialog,
            captcha = uiState.captcha,
            isLoading = uiState.isLoadingCaptcha,
            isVerifying = uiState.isVerifyingCaptcha,
            onDismiss = viewModel::dismissCaptchaDialog,
            onRefreshCaptcha = viewModel::refreshCaptcha,
            onConfirm = { captchaCode ->
                viewModel.verifyCaptchaAndSendCode(
                    captchaCode = captchaCode,
                    onSuccess = null
                )
            }
        )
    }
}

/**
 * 验证码输入界面
 */
@Composable
fun CodeVerifyScreen(
    email: String,
    verificationCode: String = "",
    isLoggingIn: Boolean = false,
    canLogin: Boolean = false,
    resendCountdown: Int = 0,
    onCodeChange: (String) -> Unit = {},
    onSendVerificationCode: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val subtitleText = stringResource(id = R.string.code_sent_to, email)

    ModernAuthScaffold(
        title = stringResource(id = R.string.input_code_title),
        subtitle = subtitleText,
        showNavigationIcon = true,
        onNavigationClick = onBackClick
    ) {
        ModernCodeBoxes(
            code = verificationCode,
            onCodeChange = onCodeChange,
            onComplete = {
                onLoginClick()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (resendCountdown > 0) {
                Text(
                    text = stringResource(id = R.string.resend_countdown_format, resendCountdown),
                    color = PrimaryDefault,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = stringResource(id = R.string.resend_verification_code),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDefault,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSendVerificationCode
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 登录按钮（服务端自动处理：已注册直接登录，未注册自动创建账号并返回Token）
        ModernAuthButton(
            text = if (isLoggingIn) stringResource(id = R.string.logging_in) else stringResource(id = R.string.login),
            enabled = canLogin,
            isLoading = isLoggingIn,
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.code_tip),
            fontSize = 12.sp,
            color = Color(0xFF8F959E),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )
    }
}

// ======================================================================================
// 3. 密码输入步骤（第二步：输入密码并登录）
// ======================================================================================

/**
 * 输入密码路由
 */
@Composable
fun PasswordInputRoute(
    email: String,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LaunchedEffect(email) {
        viewModel.initEmail(email)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PasswordInputScreen(
        email = email,
        password = uiState.password,
        isLoggingIn = uiState.isLoggingIn,
        canLogin = uiState.canPasswordLogin,
        onPasswordChange = viewModel::updatePassword,
        onLoginClick = { viewModel.loginByPassword() },
        onBackClick = { navigateBack() }
    )
}

/**
 * 输入密码界面
 */
@Composable
fun PasswordInputScreen(
    email: String,
    password: String = "",
    isLoggingIn: Boolean = false,
    canLogin: Boolean = false,
    onPasswordChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    ModernAuthScaffold(
        title = stringResource(id = R.string.input_password_title),
        subtitle = stringResource(id = R.string.account_label, email),
        showNavigationIcon = true,
        onNavigationClick = onBackClick
    ) {
        ModernPasswordInput(
            password = password,
            onPasswordChange = onPasswordChange,
            placeholder = stringResource(id = R.string.password_hint),
            onDone = {
                if (canLogin) {
                    onLoginClick()
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 登录按钮
        ModernAuthButton(
            text = if (isLoggingIn) stringResource(id = R.string.logging_in) else stringResource(id = R.string.login),
            enabled = canLogin,
            isLoading = isLoggingIn,
            onClick = onLoginClick
        )
    }
}
