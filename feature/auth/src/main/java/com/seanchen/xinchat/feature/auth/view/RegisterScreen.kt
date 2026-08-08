package com.seanchen.xinchat.feature.auth.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.viewmodel.RegisterUiState
import com.seanchen.xinchat.feature.auth.viewmodel.RegisterViewModel

@Composable
internal fun RegisterRoute(
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    RegisterScreen(
        uiState = uiState,
        onUsernameChanged = viewModel::onUsernameChanged,
        onDisplayNameChanged = viewModel::onDisplayNameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onRegisterClick = viewModel::register,
        onBack = onBack,
    )
}



@Composable
internal fun RegisterScreen(
    uiState: RegisterUiState,
    onUsernameChanged: (String) -> Unit,
    onDisplayNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.register_title), style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(24.dp))
        RegisterField(uiState.username, onUsernameChanged, R.string.username, uiState.isSubmitting)
        RegisterField(
            uiState.displayName,
            onDisplayNameChanged,
            R.string.display_name,
            uiState.isSubmitting,
        )
        RegisterField(
            uiState.email,
            onEmailChanged,
            R.string.email,
            uiState.isSubmitting,
            keyboardType = KeyboardType.Email,
        )
        RegisterField(
            uiState.password,
            onPasswordChanged,
            R.string.password,
            uiState.isSubmitting,
            password = true,
        )
        RegisterField(
            uiState.confirmPassword,
            onConfirmPasswordChanged,
            R.string.confirm_password,
            uiState.isSubmitting,
            password = true,
        )
        if (uiState.confirmPassword.isNotEmpty() && uiState.password != uiState.confirmPassword) {
            Text(
                stringResource(R.string.password_mismatch),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        uiState.errorMessage?.let { message ->
            Text(
                message,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.canSubmit,
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.register))
            }
        }
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSubmitting,
        ) {
            Text(stringResource(R.string.back_to_login))
        }
    }
}

@Composable
private fun RegisterField(
    value: String,
    onValueChange: (String) -> Unit,
    labelRes: Int,
    submitting: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        label = { Text(stringResource(labelRes)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (password) PasswordVisualTransformation() else {
            androidx.compose.ui.text.input.VisualTransformation.None
        },
        enabled = !submitting,
        singleLine = true,
    )
}
