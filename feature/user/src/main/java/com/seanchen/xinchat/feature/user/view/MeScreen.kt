package com.seanchen.xinchat.feature.user.view

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.ui.component.scaffold.CommonScaffold
import com.seanchen.xinchat.feature.user.viewmodel.MeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.seanchen.xinchat.core.designsystem.component.VerticalList
import com.seanchen.xinchat.core.model.entity.User

@Composable
internal fun MeRoute(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    viewModel: MeViewModel = hiltViewModel(),
){
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()

    MeScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        isLoggedIn = isLoggedIn,
        userInfo = userInfo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MeScreen(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    isLoggedIn: Boolean = false,
    userInfo: User? = null
){
    CommonScaffold(topBar = {}) { paddingValues ->
        MeContentView(
            isLoggedIn = isLoggedIn,
            userInfo = userInfo ?: User(),
            modifier = Modifier.padding(paddingValues),
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
        )
    }
}


@Composable
private fun MeContentView(
    isLoggedIn: Boolean,
    userInfo: User?,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
){
    VerticalList(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        UserInfoSection()
    }
}


@Composable
private fun UserInfoSection(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    isLoggedIn: Boolean,
    userInfo: User?
){
    Row() { }
}