package com.seanchen.xinchat.feature.auth.ui

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
