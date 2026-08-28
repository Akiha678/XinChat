package com.seanchen.xinchat.feature.user.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.designsystem.component.VerticalList
import com.seanchen.xinchat.core.navigation.NavigationOptions
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.core.navigation.navigate
import com.seanchen.xinchat.core.ui.component.scaffold.CommonScaffold
import com.seanchen.xinchat.feature.user.component.FunctionMenuSection
import com.seanchen.xinchat.feature.user.viewmodel.ProfileViewModel

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel()
){
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    // 收集登录状态
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isLoggingOut by viewModel.isLoggingOut.collectAsStateWithLifecycle()
    val logoutCompleted by viewModel.logoutCompleted.collectAsStateWithLifecycle()

    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            navigate(
                route = AuthRoutes.Login,
                navOptions = NavigationOptions(
                    popUpToRoute = MainRoutes.Main,
                    inclusive = true,
                    allowPopToEmpty = true
                )
            )
        }
    }


    ProfileScreen(
        isLoggedIn = isLoggedIn,
        isLoggingOut = isLoggingOut,
        onLogoutClick = viewModel::logout
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    isLoggedIn: Boolean = false,
    isLoggingOut: Boolean = false,
    onLogoutClick: () -> Unit = {}
){
    CommonScaffold(topBar = {}) { paddingValues ->
        ProfileContentView(
            modifier = Modifier.padding(paddingValues),
            isLoggedIn = isLoggedIn,
            isLoggingOut = isLoggingOut,
            onLogoutClick = onLogoutClick
        )
    }
}

@Composable
private fun ProfileContentView(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    isLoggingOut: Boolean,
    onLogoutClick: () -> Unit,
){

    VerticalList(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        FunctionMenuSection(
            isLoggedIn = isLoggedIn,
            isLoggingOut = isLoggingOut,
            onLogoutClick = onLogoutClick
        )
    }
}
