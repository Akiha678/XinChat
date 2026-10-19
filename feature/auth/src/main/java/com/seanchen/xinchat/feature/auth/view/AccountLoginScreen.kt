package com.seanchen.xinchat.feature.auth.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.navigation.auth.AuthNavigator
import com.seanchen.xinchat.core.navigation.common.CommonNavigator
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.core.util.validation.ValidationUtil
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.ModernAuthAgreement
import com.seanchen.xinchat.feature.auth.component.ModernAuthBranding
import com.seanchen.xinchat.feature.auth.component.ModernAuthButton
import com.seanchen.xinchat.feature.auth.component.ModernAuthScaffold
import com.seanchen.xinchat.feature.auth.component.ModernEmailInput
import com.seanchen.xinchat.feature.auth.viewmodel.AccountLoginViewModel

/**
 * 邮箱密码登录 - 第一步（输入邮箱）
 */
@Composable
internal fun AccountLoginRoute(
    viewModel: AccountLoginViewModel = run {
        val activity = LocalContext.current as? ComponentActivity
        if (activity != null) hiltViewModel(activity) else hiltViewModel()
    }
) {
    val account by viewModel.account.collectAsStateWithLifecycle()
    val context = LocalContext.current

    AccountLoginScreen(
        account = account,
        onAccountChange = viewModel::updateAccount,
        onCloseClick = {
            val activity = context as? ComponentActivity
            if (activity != null) {
                activity.onBackPressedDispatcher.onBackPressed()
            } else {
                navigateBack()
            }
        },
        onToSmsLoginClick = { AuthNavigator.toLogin() },
        onNextClick = { targetAccount -> AuthNavigator.toPasswordInput(targetAccount) }
    )
}

@Composable
internal fun AccountLoginScreen(
    account: String = "",
    onAccountChange: (String) -> Unit = {},
    onCloseClick: () -> Unit = {},
    onToSmsLoginClick: () -> Unit = {},
    onNextClick: (String) -> Unit = {}
) {
    var isAgreed by remember { mutableStateOf(false) }
    val isEmailValid = ValidationUtil.isValidEmail(account)

    ModernAuthScaffold(
        title = stringResource(id = R.string.account_login),
        isCloseAction = true,
        onNavigationClick = onCloseClick
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 邮箱输入框
        ModernEmailInput(
            email = account,
            onEmailChange = onAccountChange,
            placeholder = stringResource(id = R.string.email_hint),
            onDone = {
                if (isEmailValid) {
                    if (!isAgreed) {
                        ToastUtils.showError(R.string.please_agree_agreement)
                    } else {
                        onNextClick(account)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 下一步主按钮
        ModernAuthButton(
            text = stringResource(id = R.string.next_step),
            enabled = isEmailValid,
            onClick = {
                if (!isAgreed) {
                    ToastUtils.showError(R.string.please_agree_agreement)
                } else {
                    onNextClick(account)
                }
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
                color = Color(0xFF646A73),
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToSmsLoginClick
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 协议勾选行
        ModernAuthAgreement(
            isAgreed = isAgreed,
            onAgreedChange = { isAgreed = it },
            onUserAgreementClick = CommonNavigator::toUserAgreement,
            onPrivacyPolicyClick = CommonNavigator::toPrivacyPolicy
        )

        // 底部品牌标识（Logo + XinChat）
        ModernAuthBranding()
    }
}

@Preview(name = "邮箱密码登录默认状态", showBackground = true)
@Composable
private fun AccountLoginScreenPreview() {
    AccountLoginScreen()
}

@Preview(name = "邮箱密码登录已输入状态", showBackground = true)
@Composable
private fun AccountLoginScreenFilledPreview() {
    AccountLoginScreen(account = "test@example.com")
}
