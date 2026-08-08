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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.feature.auth.R
import com.seanchen.xinchat.feature.auth.viewmodel.LoginUiState
import com.seanchen.xinchat.feature.auth.viewmodel.LoginViewModel


@Composable
internal fun LoginRoute(
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    LoginScreen(
        uiState = uiState,
        onUsernameChanged = viewModel::onUsernameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginClick = viewModel::login,
        onRegisterClick = onRegisterClick,
    )
}


@Composable
internal fun LoginScreen(
    uiState: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.login_welcome), style = MaterialTheme.typography.headlineLarge)
        Text(
            stringResource(R.string.login_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUsernameChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.username)) },
            singleLine = true,
            enabled = !uiState.isSubmitting,
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !uiState.isSubmitting,
        )
        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.username.isNotBlank() && uiState.password.isNotBlank() &&
                !uiState.isSubmitting,
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.login))
            }
        }
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSubmitting,
        ) {
            Text(stringResource(R.string.go_to_register))
        }
    }
}

@Composable
fun LoginContentView(
    uiState: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,

    ){


    /**
     * 登录输入框
     */
    Column(

    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !uiState.isSubmitting,
        )


        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.username,
            onValueChange = onUsernameChanged,
            label = { Text(stringResource(R.string.username)) },
            singleLine = true,
            enabled = !uiState.isSubmitting
        )

    }
}

/**
 * 登录界面
 */
@Composable
fun LoginContentView(){}



/**
 * 账号密码登录
 */
@Composable
fun AccountLoginContentView(){}


/**
 * 验证码登录
 */
@Composable
fun VerifyLoginContentView(){}
