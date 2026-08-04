package com.seanchen.xinchat.feature.user.ui

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun ProfileRoute(viewModel: ProfileViewModel = hiltViewModel()) {
    ProfileScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onLogout = viewModel::logout,
    )
}
