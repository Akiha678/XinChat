package com.seanchen.xinchat

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.seanchen.xinchat.core.navigation.AppNavigator
import com.seanchen.xinchat.core.navigation.TopLevelNavKey
import com.seanchen.xinchat.feature.auth.navigation.LoginKey
import com.seanchen.xinchat.feature.auth.navigation.RegisterKey
import com.seanchen.xinchat.feature.auth.navigation.authEntries
import com.seanchen.xinchat.feature.auth.navigation.authGraph
import com.seanchen.xinchat.feature.chat.navigation.ConversationKey
import com.seanchen.xinchat.feature.chat.navigation.MessagesKey
import com.seanchen.xinchat.feature.chat.navigation.messagesEntry
import com.seanchen.xinchat.feature.contact.navigation.ContactsKey
import com.seanchen.xinchat.feature.contact.navigation.contactsEntry
import com.seanchen.xinchat.feature.user.navigation.ProfileKey
import com.seanchen.xinchat.feature.user.navigation.profileEntry

private val topLevelDestinations = listOf(
    TopLevelDestination(MessagesKey, R.string.navigation_messages, R.drawable.ic_messages),
    TopLevelDestination(ContactsKey, R.string.navigation_contacts, R.drawable.ic_contacts),
    TopLevelDestination(ProfileKey, R.string.navigation_profile, R.drawable.ic_profile),
)

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    when {
        uiState.isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        uiState.isLoggedIn -> MainNavHost(modifier)
        else -> AuthNavHost(modifier)
    }
}

@Composable
private fun AuthNavHost(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(LoginKey)
    val entries = rememberTopLevelEntries(
        backStack = backStack,
        entryProvider = entryProvider<NavKey> {
            authEntries(
                onRegisterClick = {
                    if (backStack.lastOrNull() != RegisterKey) backStack.add(RegisterKey)
                },
                onRegisterBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
            )
        },
    )
    NavDisplay(
        entries = entries,
        modifier = modifier.fillMaxSize(),
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
    )
}

@Composable
private fun MainNavHost(modifier: Modifier = Modifier) {
    val navigator = rememberAppNavigator()
    val entryProvider = entryProvider<NavKey> {
        messagesEntry(
            onConversationClick = navigator::navigateTo,
            onConversationBack = { navigator.pop() },
        )
        contactsEntry { conversationId, name ->
            navigator.navigateTo(MessagesKey)
            navigator.navigateTo(ConversationKey(conversationId, name))
        }
        profileEntry()
    }

    val messagesEntries = rememberTopLevelEntries(
        navigator.backStackFor(MessagesKey),
        entryProvider,
    )
    val contactsEntries = rememberTopLevelEntries(
        navigator.backStackFor(ContactsKey),
        entryProvider,
    )
    val profileEntries = rememberTopLevelEntries(
        navigator.backStackFor(ProfileKey),
        entryProvider,
    )
    val currentDestination = navigator.currentDestination
    val currentEntries = when (currentDestination) {
        MessagesKey -> messagesEntries
        ContactsKey -> contactsEntries
        ProfileKey -> profileEntries
        else -> error("未知顶级目的地：${currentDestination.route}")
    }
    val showBottomBar = navigator.currentBackStack.lastOrNull() is TopLevelNavKey

    BackHandler(
        enabled = !navigator.canNavigateBack && currentDestination != navigator.startDestination,
        onBack = navigator::navigateBackToStart,
    )
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val label = stringResource(destination.labelRes)
                        NavigationBarItem(
                            selected = currentDestination == destination.key,
                            onClick = { navigator.navigateTo(destination.key) },
                            icon = {
                                Icon(
                                    painterResource(destination.iconRes),
                                    contentDescription = label,
                                )
                            },
                            label = { Text(label) },
                        )
                    }
                }
            }
        },
    ) { contentPadding ->
        NavDisplay(
            entries = currentEntries,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding),
            onBack = { navigator.pop() },
        )
    }
}

@Composable
private fun rememberAppNavigator(): AppNavigator {
    val selectedRoute = rememberSaveable { mutableStateOf(MessagesKey.route) }
    val messagesBackStack = rememberNavBackStack(MessagesKey)
    val contactsBackStack = rememberNavBackStack(ContactsKey)
    val profileBackStack = rememberNavBackStack(ProfileKey)
    return remember(selectedRoute, messagesBackStack, contactsBackStack, profileBackStack) {
        AppNavigator(
            selectedRoute = selectedRoute,
            backStacks = mapOf(
                MessagesKey to messagesBackStack,
                ContactsKey to contactsBackStack,
                ProfileKey to profileBackStack,
            ),
            startDestination = MessagesKey,
        )
    }
}

@Composable
private fun rememberTopLevelEntries(
    backStack: NavBackStack<NavKey>,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): List<NavEntry<NavKey>> {
    val decorators = listOf<NavEntryDecorator<NavKey>>(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
    )
    return rememberDecoratedNavEntries(backStack, decorators, entryProvider)
}

private data class TopLevelDestination(
    val key: TopLevelNavKey,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
)



private fun appEntryProvider(sharedTransitionScope: SharedTransitionScope) = entryProvider {
    authGraph()
}