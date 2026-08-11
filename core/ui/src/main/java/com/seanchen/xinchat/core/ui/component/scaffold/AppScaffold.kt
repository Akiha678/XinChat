package com.seanchen.xinchat.core.ui.component.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.seanchen.xinchat.core.ui.component.appbar.CenterTopAppBar
import com.seanchen.xinchat.core.ui.component.appbar.LargeTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    title: Int? = null,
    titleText: String? = null,
    topBarColors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    topBarActions: @Composable (RowScope.() -> Unit) = {},
    showBackIcon: Boolean = true,
    onBackClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    topBar: @Composable (() -> Unit)? = null,
    useLargeTopBar: Boolean = false,
    largeTopBarExpandedBackgroundColor: Color = MaterialTheme.colorScheme.background,
    largeTopBarCollapsedBackgroundColor: Color = MaterialTheme.colorScheme.background,
    contentShouldConsumePadding: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
){
    val scrollBehavior = if (useLargeTopBar) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    } else null
    
    val finalModifier = if (scrollBehavior != null) {
        modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    } else modifier

    Scaffold(
        topBar = {
            if (topBar != null) {
                topBar()
            } else if (useLargeTopBar) {
                LargeTopAppBar(
                    title = title,
                    titleText = titleText,
                    actions = topBarActions,
                    onBackClick = onBackClick,
                    showBackIcon = showBackIcon,
                    scrollBehavior = scrollBehavior,
                    expandedBackgroundColor = largeTopBarExpandedBackgroundColor,
                    collapsedBackgroundColor = largeTopBarCollapsedBackgroundColor
                )
            } else {
                CenterTopAppBar(
                    title = title,
                    titleText = titleText,
                    colors = topBarColors,
                    actions = topBarActions,
                    onBackClick = onBackClick,
                    showBackIcon = showBackIcon
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        modifier = finalModifier,
        content = { paddingValues ->
            val boxModifier = if (contentShouldConsumePadding) {
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            } else {
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
            }
            Box(
                modifier = boxModifier
            ) {
                content(paddingValues)
            }
        }
    )
}