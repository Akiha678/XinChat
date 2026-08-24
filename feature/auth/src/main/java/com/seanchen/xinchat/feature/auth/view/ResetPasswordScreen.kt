package com.seanchen.xinchat.feature.auth.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.seanchen.xinchat.core.designsystem.component.StartRow
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXLarge
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.button.AppButton
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.component.AnimatedAuthPage
import com.seanchen.xinchat.feature.auth.component.PasswordInputField
import com.seanchen.xinchat.feature.auth.component.PhoneInputField
import com.seanchen.xinchat.feature.auth.component.VerificationCodeField
import com.seanchen.xinchat.feature.auth.viewmodel.ResetPasswordViewModel

@Composable
internal fun ResetPasswordRoute(
    viewModel: ResetPasswordViewModel = hiltViewModel()
){
    val phone by viewModel.phone.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val verificationCode by viewModel.verificationCode.collectAsState()

    ResetPasswordScreen(
        phone = phone,
        newPassword = newPassword,
        confirmPassword = confirmPassword,
        verificationCode = verificationCode,
        onPhoneChange = viewModel::updatePhone,
        onNewPasswordChange = viewModel::updateNewPassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onVerificationCodeChange = viewModel::updateVerificationCode,
        onSendVerificationCode = viewModel::sendVerificationCode,
        onResetPasswordClick = viewModel::resetPassword
    )
}


@Composable
internal fun ResetPasswordScreen(
    phone: String = "",
    newPassword: String = "",
    confirmPassword: String = "",
    verificationCode: String = "",
    onPhoneChange: (String) -> Unit = {},
    onNewPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onVerificationCodeChange: (String) -> Unit = {},
    onSendVerificationCode: () -> Unit = {},
    onResetPasswordClick: () -> Unit = {}
){
    AnimatedAuthPage(
        title = stringResource(id = R.string.reset_password),
        withFadeIn = true,
        onBackClick = { navigateBack() }
    ) {
        ResetPasswordContentView(
            phone = phone,
            newPassword = newPassword,
            confirmPassword = confirmPassword,
            verificationCode = verificationCode,
            onPhoneChange = onPhoneChange,
            onNewPasswordChange = onNewPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onVerificationCodeChange = onVerificationCodeChange,
            onSendVerificationCode = onSendVerificationCode,
            onResetPasswordClick = onResetPasswordClick
        )
    }
}

@Composable
private fun ResetPasswordContentView(
    phone: String,
    newPassword: String,
    confirmPassword: String,
    verificationCode: String,
    onPhoneChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onVerificationCodeChange: (String) -> Unit,
    onSendVerificationCode: () -> Unit,
    onResetPasswordClick: () -> Unit
){
    val phoneFieldFocused = remember { mutableStateOf(false) }
    val codeFieldFocused = remember { mutableStateOf(false) }
    val newPasswordFieldFocused = remember { mutableStateOf(false) }
    val confirmPasswordFieldFocused = remember { mutableStateOf(false) }

    PhoneInputField(
        phone = phone,
        onPhoneChange = onPhoneChange,
        phoneFieldFocused = phoneFieldFocused,
        placeholder = stringResource(id = R.string.phone_hint),
        nextAction = ImeAction.Next // 回车改为下一项
    )

    Spacer(modifier = Modifier.height(30.dp))

    VerificationCodeField(
        verificationCode = verificationCode,
        onVerificationCodeChange = onVerificationCodeChange,
        codeFieldFocused = codeFieldFocused,
        onSendVerificationCode = onSendVerificationCode,
        placeholder = stringResource(id = R.string.verification_code),
        nextAction = ImeAction.Next
    )

    Spacer(modifier = Modifier.height(30.dp))

    PasswordInputField(
        password = newPassword,
        onPasswordChange = onNewPasswordChange,
        passwordFieldFocused = newPasswordFieldFocused,
        placeholder = stringResource(id = R.string.set_new_password),
        nextAction = ImeAction.Next
    )

    Spacer(modifier = Modifier.height(30.dp))

    PasswordInputField(
        password = confirmPassword,
        onPasswordChange = onConfirmPasswordChange,
        passwordFieldFocused = confirmPasswordFieldFocused,
        placeholder = stringResource(id = R.string.confirm_new_password),
        nextAction = ImeAction.Done
    )

    SpaceVerticalMedium()

    StartRow {
        Text(
            text = stringResource(id = R.string.reset_password_tip),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }

    if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
        SpaceVerticalMedium()

        Text(
            text = stringResource(id = R.string.password_mismatch),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }

    SpaceVerticalXLarge()

    AppButton(
        text = stringResource(id = R.string.reset_password),
        onClick = onResetPasswordClick,
        enabled = false
    )
}