package com.seanchen.xinchat.feature.auth.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.ModernAuthButton
import com.seanchen.xinchat.feature.auth.component.ModernAuthScaffold
import com.seanchen.xinchat.feature.auth.component.ModernCodeBoxes
import com.seanchen.xinchat.feature.auth.state.SmsLoginUiState
import com.seanchen.xinchat.feature.auth.viewmodel.SmsLoginViewModel

/**
 * 邮箱验证码登录 - 第二步（输入验证码）
 */
@Composable
fun SmsCodeVerifyRoute(
    phone: String, // 对应邮箱地址
    viewModel: SmsLoginViewModel = run {
        val activity = LocalContext.current as? ComponentActivity
        if (activity != null) hiltViewModel(activity) else hiltViewModel()
    }
) {
    LaunchedEffect(phone) {
        viewModel.initEmail(phone)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SmsCodeVerifyScreen(
        email = if (uiState.email.isNotEmpty()) uiState.email else phone,
        verificationCode = uiState.verificationCode,
        resendCountdown = uiState.resendCountdown,
        isLoggingIn = uiState.isLoggingIn,
        onCodeChange = viewModel::updateVerificationCode,
        onSendVerificationCode = viewModel::sendVerificationCode,
        onLoginClick = { viewModel.login() },
        onBackClick = { navigateBack() }
    )
}

@Composable
fun SmsCodeVerifyScreen(
    email: String,
    verificationCode: String = "",
    resendCountdown: Int = 0,
    isLoggingIn: Boolean = false,
    onCodeChange: (String) -> Unit = {},
    onSendVerificationCode: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val subtitleText = stringResource(id = R.string.sms_code_sent_to, email)
    val isCodeComplete = verificationCode.length == 4

    ModernAuthScaffold(
        title = stringResource(id = R.string.input_sms_code_title),
        subtitle = subtitleText,
        isCloseAction = false,
        onNavigationClick = onBackClick
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 4位验证码输入方框
        ModernCodeBoxes(
            code = verificationCode,
            onCodeChange = onCodeChange,
            codeLength = 4,
            onComplete = {
                onLoginClick()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 重新获取倒计时与操作链接
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (resendCountdown > 0) {
                Text(
                    text = stringResource(id = R.string.resend_countdown_format, resendCountdown),
                    color = Color(0xFF8F959E),
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = stringResource(id = R.string.resend_verification_code),
                    color = Color(0xFF1F2329),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSendVerificationCode
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // 登录按钮
        ModernAuthButton(
            text = if (isLoggingIn) "登录中..." else stringResource(id = R.string.login),
            enabled = isCodeComplete,
            isLoading = isLoggingIn,
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "如未收到验证码，请检查邮箱垃圾箱或稍后重试",
            color = Color(0xFFC4C7CC),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )
    }
}

@Preview(name = "邮箱验证码输入页", showBackground = true)
@Composable
private fun SmsCodeVerifyScreenPreview() {
    SmsCodeVerifyScreen(
        email = "test@example.com",
        verificationCode = "12",
        resendCountdown = 52
    )
}
