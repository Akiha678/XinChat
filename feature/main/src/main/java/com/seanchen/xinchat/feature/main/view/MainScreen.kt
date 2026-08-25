package com.seanchen.xinchat.feature.main.view

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.seanchen.xinchat.feature.chat.view.ChatListRoute
import com.seanchen.xinchat.feature.main.component.BottomNavigationBar
import com.seanchen.xinchat.feature.main.model.TopLevelDestination
import com.seanchen.xinchat.feature.main.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import com.seanchen.xinchat.feature.contact.view.ContactRoute
import com.seanchen.xinchat.feature.user.view.ProfileRoute

@Composable
internal fun MainRoute(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    viewModel: MainViewModel = hiltViewModel()
) {
    val currentPageIndex by viewModel.currentPageIndex.collectAsState()

    MainScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        currentPageIndex = currentPageIndex,
        onPageChanged = viewModel::updatePageIndex,
        onNavigationItemSelected = viewModel::updateDestination
    )
}

@Composable
internal fun MainScreen(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    currentPageIndex: Int = 0,
    onPageChanged: (Int) -> Unit = {},
    onNavigationItemSelected: (Int) -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    val pageState = rememberPagerState(
        initialPage = currentPageIndex
    ) {
        TopLevelDestination.entries.size
    }

    LaunchedEffect(pageState.currentPage) {
        onPageChanged(pageState.currentPage)
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars),
        bottomBar = {
            BottomNavigationBar(
                destinations = TopLevelDestination.entries,
                onNavigateToDestination = { index ->
                    onNavigationItemSelected(index)
                    scope.launch {
                        pageState.scrollToPage(index)
                    }
                },
                currentPageIndex = currentPageIndex,
                modifier = Modifier
            )
        }
    ) { paddingValues ->
        MainScreenContentView(
            pageState = pageState,
            paddingValues = paddingValues,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )
    }
}

@Composable
private fun MainScreenContentView(
    pageState: PagerState,
    paddingValues: PaddingValues,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
) {
    HorizontalPager(
        state = pageState,
        modifier = Modifier.padding(paddingValues)
    ) { page: Int ->
        when (page) {
            0 -> ChatListRoute()
            1 -> ContactRoute()
            2 -> ProfileRoute()
        }
    }
}