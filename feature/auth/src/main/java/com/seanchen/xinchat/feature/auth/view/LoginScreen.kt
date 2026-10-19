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
import com.seanchen.xinchat.feature.auth.component.ModernAuthAgreement
import com.seanchen.xinchat.feature.auth.component.ModernAuthBranding
import com.seanchen.xinchat.feature.auth.component.ModernAuthButton
import com.seanchen.xinchat.feature.auth.component.ModernAuthScaffold
import com.seanchen.xinchat.feature.auth.component.ModernCodeBoxes
import com.seanchen.xinchat.feature.auth.component.ModernEmailInput
import com.seanchen.xinchat.feature.auth.component.ModernPasswordInput
import com.seanchen.xinchat.feature.auth.state.LoginUiState
import com.seanchen.xinchat.feature.auth.viewmodel.LoginViewModel

// ======================================================================================
// 1. 验证码登录流程 (Email Code Login)
// ======================================================================================

/**
 * 验证码登录路由（第一步：输入邮箱）
 */
@Composable
fun CodeLoginRoute(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CodeLoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::updateEmail,
        onAgreedChange = viewModel::updateAgreed,
        onSendVerificationCode = {
            viewModel.sendVerificationCode(requireAgreement = true) {
                AuthNavigator.toSmsCodeVerify(uiState.email)
            }
        },
        onToPasswordLoginClick = {
            AuthNavigator.toAccountLogin()
        },
        onCloseClick = {
            navigateBack()
        }
    )
}
/**
 * 验证码登录界面（输入邮箱）
 */
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
    ModernAuthScaffold(
        title = stringResource(id = R.string.sms_login),
        isCloseAction = true,
        onNavigationClick = onCloseClick
    ) {
        ModernEmailInput(
            email = uiState.email,
            onEmailChange = onEmailChange,
            placeholder = stringResource(id = R.string.email_hint),
            onDone = {
                if (uiState.isEmailValid) {
                    onSendVerificationCode()
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

        // 获取验证码按钮
        ModernAuthButton(
            text = stringResource(id = R.string.get_verification_code),
            enabled = uiState.isEmailValid,
            isLoading = uiState.isSendingCode,
            onClick = onSendVerificationCode
        )

        // 切换到密码登录
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(id = R.string.account_login),
                color = PrimaryDefault,
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToPasswordLoginClick
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 底部品牌
        ModernAuthBranding(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

/**
 * 验证码输入路由（第二步：输入验证码）
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
        onSendVerificationCode = { viewModel.sendVerificationCode(requireAgreement = false) },
        onLoginClick = { viewModel.loginByCode() },
        onBackClick = { navigateBack() }
    )
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
    val subtitleText = stringResource(id = R.string.sms_code_sent_to, email)

    ModernAuthScaffold(
        title = stringResource(id = R.string.input_sms_code_title),
        subtitle = subtitleText,
        isCloseAction = false,
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

        // 登录按钮
        ModernAuthButton(
            text = if (isLoggingIn) stringResource(id = R.string.logging_in) else stringResource(id = R.string.login),
            enabled = canLogin,
            isLoading = isLoggingIn,
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.sms_code_tip),
            fontSize = 12.sp,
            color = Color(0xFF8F959E),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )
    }
}

// ======================================================================================
// 2. 密码登录流程 (Email Password Login)
// ======================================================================================

/**
 * 密码登录路由（第一步：输入邮箱）
 */
@Composable
fun PasswordLoginRoute(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PasswordLoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::updateEmail,
        onAgreedChange = viewModel::updateAgreed,
        onNextClick = {
            if (!uiState.isAgreed) {
                runCatching { ToastUtils.showError(R.string.please_agree_agreement) }
            } else {
                AuthNavigator.toPasswordInput(uiState.email)
            }
        },
        onToSmsLoginClick = {
            AuthNavigator.toSmsLogin()
        },
        onCloseClick = {
            navigateBack()
        }
    )
}

/**
 * 密码登录界面（第一步：输入邮箱）
 */
@Composable
fun PasswordLoginScreen(
    uiState: LoginUiState = LoginUiState(),
    onEmailChange: (String) -> Unit = {},
    onAgreedChange: (Boolean) -> Unit = {},
    onNextClick: (String) -> Unit = {},
    onToSmsLoginClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {}
) {
    ModernAuthScaffold(
        title = stringResource(id = R.string.account_login),
        isCloseAction = true,
        onNavigationClick = onCloseClick
    ) {
        ModernEmailInput(
            email = uiState.email,
            onEmailChange = onEmailChange,
            placeholder = stringResource(id = R.string.email_hint),
            onDone = {
                if (uiState.isEmailValid) {
                    onNextClick(uiState.email)
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

        // 下一步按钮
        ModernAuthButton(
            text = stringResource(id = R.string.next_step),
            enabled = uiState.isEmailValid,
            onClick = {
                onNextClick(uiState.email)
            }
        )

        // 辅助链接：切换到“验证码登录”
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(id = R.string.sms_login),
                color = PrimaryDefault,
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToSmsLoginClick
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 底部品牌
        ModernAuthBranding(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}


/**
 * 输入密码路由（第二步：输入密码）
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
        isCloseAction = false,
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
