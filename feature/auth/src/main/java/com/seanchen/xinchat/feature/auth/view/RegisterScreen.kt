package com.seanchen.xinchat.feature.auth.view


import android.app.Activity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.seanchen.xinchat.core.designsystem.component.BottomNavigationRow
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXLarge
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.navigation.common.CommonNavigator
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.button.AppButton
import com.seanchen.xinchat.core.util.permission.PermissionUtil
import com.seanchen.xinchat.core.util.toast.ToastUtils
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.AnimatedAuthPage
import com.seanchen.xinchat.feature.auth.component.ImageCaptchaDialog
import com.seanchen.xinchat.feature.auth.component.PasswordInputField
import com.seanchen.xinchat.feature.auth.component.PhoneInputField
import com.seanchen.xinchat.feature.auth.component.UserAgreement
import com.seanchen.xinchat.feature.auth.component.VerificationCodeField
import com.seanchen.xinchat.feature.auth.viewmodel.RegisterViewModel

@Composable
internal fun RegisterRoute(
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    // 收集手机号输入
    val phone by viewModel.phone.collectAsState()
    // 收集验证码输入
    val verificationCode by viewModel.verificationCode.collectAsState()
    // 收集密码输入
    val password by viewModel.password.collectAsState()
    // 收集确认密码输入
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    // 收集图片验证码弹窗显示状态
    val showImageCodePopup by viewModel.showImageCodePopup.collectAsState()
    // 收集图片验证码数据
    val captcha by viewModel.captcha.collectAsState()
    // 收集验证码加载状态
    val isLoadingCaptcha by viewModel.isLoadingCaptcha.collectAsState()
    // 收集手机号验证状态
    val isPhoneValid by viewModel.isPhoneValid.collectAsState(initial = false)
    // 收集注册按钮启动状态
    val isRegisterEnabled by viewModel.isRegisterEnabled.collectAsState(initial = false)

    val onSendVerificationCodeWithPermission = {
        if (context is Activity) {
            PermissionUtil.requestNotificationPermission(context) { granted ->
                if (granted) {
                    viewModel.onSendCodeButtonClick()
                } else {
                    ToastUtils.showError(R.string.notification_permission_required)
                }
            }
        } else {
            viewModel.onSendCodeButtonClick()
        }
    }

    RegisterScreen()
}

//@OptIn(ExperimentalMaterial3Api)
@Composable
internal fun RegisterScreen(
    phone: String = "",
    verificationCode: String = "",
    password: String = "",
    confirmPassword: String = "",
    showImageCodePopup: Boolean = false,
    captcha: Captcha = Captcha(),
    isLoadingCaptcha: Boolean = false,
    isPhoneValid: Boolean = false,
    isRegisterEnabled: Boolean = false,
    onHideImageCodePopup: () -> Unit = {},
    onPhoneChange: (String) -> Unit = {},
    onVerificationCodeChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSendVerificationCode: () -> Unit = {},
    onImageCodeConfirm: (String) -> Unit = {},
    onRefreshCaptcha: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    AnimatedAuthPage(
        title = stringResource(id = R.string.welcome_register),
        withFadeIn = true,
        onBackClick = { navigateBack() }
    ) {
        RegisterContentView(
            phone = phone,
            verificationCode = verificationCode,
            password = password,
            confirmPassword = confirmPassword,
            isPhoneValid = isPhoneValid,
            isRegisterEnabled = isRegisterEnabled,
            onPhoneChange = onPhoneChange,
            onVerificationCodeChange = onVerificationCodeChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onSendVerificationCode = onSendVerificationCode,
            onRegisterClick = onRegisterClick
        )
        ImageCaptchaDialog(
            visible = showImageCodePopup,
            captcha = captcha,
            onDismiss = onHideImageCodePopup,
            onConfirm = onImageCodeConfirm,
            onRefreshCaptcha = onRefreshCaptcha
        )
    }
}


@Composable
private fun RegisterContentView(
    phone: String,
    verificationCode: String,
    password: String,
    confirmPassword: String,
    isPhoneValid: Boolean,
    isRegisterEnabled: Boolean,
    onPhoneChange: (String) -> Unit,
    onVerificationCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSendVerificationCode: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val phoneFieldFocused = remember { mutableStateOf(false) }
    val codeFieldFocused = remember { mutableStateOf(false) }
    val passwordFieldFocused = remember { mutableStateOf(false) }
    val confirmPasswordFieldFocused = remember { mutableStateOf(false) }

    PhoneInputField(
        phone = phone,
        onPhoneChange = onPhoneChange,
        phoneFieldFocused = phoneFieldFocused,
        placeholder = stringResource(id = R.string.phone_hint),
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
        isPhoneValid = isPhoneValid
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
