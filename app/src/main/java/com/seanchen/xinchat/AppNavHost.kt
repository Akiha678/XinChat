package com.seanchen.xinchat

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.seanchen.xinchat.feature.chat.navigation.MessagesKey
import com.seanchen.xinchat.feature.chat.navigation.messagesEntry
import com.seanchen.xinchat.feature.contact.navigation.ContactsKey
import com.seanchen.xinchat.feature.contact.navigation.contactsEntry
import com.seanchen.xinchat.feature.user.navigation.ProfileKey
import com.seanchen.xinchat.feature.user.navigation.profileEntry

private val topLevelDestinations = listOf(
    TopLevelDestination(
        key = MessagesKey,
        labelRes = R.string.navigation_messages,
        iconRes = R.drawable.ic_messages,
    ),
    TopLevelDestination(
        key = ContactsKey,
        labelRes = R.string.navigation_contacts,
        iconRes = R.drawable.ic_contacts,
    ),
    TopLevelDestination(
        key = ProfileKey,
        labelRes = R.string.navigation_profile,
        iconRes = R.drawable.ic_profile,
    ),
)

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navigator = rememberAppNavigator()
    AppNavHost(navigator = navigator, modifier = modifier)
}

@Composable
internal fun AppNavHost(
    navigator: AppNavigator,
    modifier: Modifier = Modifier,
) {
    val entryProvider = remember {
        entryProvider<NavKey> {
            messagesEntry()
            contactsEntry()
            profileEntry()
        }
    }

    val messagesEntries = rememberTopLevelEntries(
        backStack = navigator.backStackFor(MessagesKey),
        entryProvider = entryProvider,
    )
    val contactsEntries = rememberTopLevelEntries(
        backStack = navigator.backStackFor(ContactsKey),
        entryProvider = entryProvider,
    )
    val profileEntries = rememberTopLevelEntries(
        backStack = navigator.backStackFor(ProfileKey),
        entryProvider = entryProvider,
    )
    val currentDestination = navigator.currentDestination
    val currentEntries = when (currentDestination) {
        MessagesKey -> messagesEntries
        ContactsKey -> contactsEntries
        ProfileKey -> profileEntries
        else -> error("Unknown top-level destination: ${currentDestination.route}")
    }

    BackHandler(
        enabled = !navigator.canNavigateBack && currentDestination != navigator.startDestination,
        onBack = navigator::navigateBackToStart,
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                topLevelDestinations.forEach { destination ->
                    val selected = currentDestination == destination.key
                    val label = stringResource(destination.labelRes)
                    NavigationBarItem(
                        selected = selected,
                        onClick = { navigator.navigateTo(destination.key) },
                        icon = {
                            Icon(
                                painter = painterResource(destination.iconRes),
                                contentDescription = label,
                            )
                        },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(),
                    )
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

    return remember(
        selectedRoute,
        messagesBackStack,
        contactsBackStack,
        profileBackStack,
    ) {
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
    return rememberDecoratedNavEntries(
        backStack = backStack,
        entryDecorators = decorators,
        entryProvider = entryProvider,
    )
}

private data class TopLevelDestination(
    val key: TopLevelNavKey,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
)
