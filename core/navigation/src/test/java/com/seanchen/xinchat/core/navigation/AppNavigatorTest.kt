package com.seanchen.xinchat.core.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppNavigatorTest {
    private val messages = TestTopLevelKey("messages")
    private val contacts = TestTopLevelKey("contacts")
    private val messagesBackStack = NavBackStack<NavKey>(messages)
    private val contactsBackStack = NavBackStack<NavKey>(contacts)
    private val navigator = AppNavigator(
        selectedRoute = mutableStateOf(messages.route),
        backStacks = mapOf(
            messages to messagesBackStack,
            contacts to contactsBackStack,
        ),
        startDestination = messages,
    )

    @Test
    fun navigateToTopLevelDestination_preservesIndependentBackStacks() {
        messagesBackStack.add(TestDetailKey("conversation-1"))

        navigator.navigateTo(contacts)

        assertEquals(contacts, navigator.currentDestination)
        assertEquals(listOf(contacts), navigator.currentBackStack)
        assertEquals(2, messagesBackStack.size)
    }

    @Test
    fun pop_doesNotRemoveTopLevelRoot() {
        assertFalse(navigator.pop())
        assertEquals(listOf(messages), messagesBackStack)
    }

    @Test
    fun navigateBackToStart_selectsStartDestinationAtAnotherTabRoot() {
        navigator.navigateTo(contacts)

        assertTrue(navigator.navigateBackToStart())
        assertEquals(messages, navigator.currentDestination)
    }

    private data class TestTopLevelKey(override val route: String) : TopLevelNavKey

    private data class TestDetailKey(val id: String) : NavKey
}
