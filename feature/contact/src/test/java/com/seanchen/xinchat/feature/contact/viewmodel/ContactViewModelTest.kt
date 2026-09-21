package com.seanchen.xinchat.feature.contact.viewmodel

import com.seanchen.xinchat.core.data.repository.ContactRepository
import com.seanchen.xinchat.core.model.request.CreateFriendRequest
import com.seanchen.xinchat.core.model.response.FriendRequestResponse
import com.seanchen.xinchat.core.model.response.UserSummaryResponse
import com.seanchen.xinchat.core.network.datadource.contact.ContactNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleFriends = listOf(
        UserSummaryResponse(id = 1L, name = "Alice", username = "alice", email = "alice@test.com", avatarColor = 0),
        UserSummaryResponse(id = 2L, name = "Bob", username = "bob", email = "bob@test.com", avatarColor = 0),
        UserSummaryResponse(id = 3L, name = "Charlie", username = "charlie", email = "charlie@test.com", avatarColor = 0)
    )

    private val fakeContactDataSource = object : ContactNetworkDataSource {
        override suspend fun searchUsers(username: String): List<UserSummaryResponse> = sampleFriends

        override suspend fun getFriends(): List<UserSummaryResponse> = sampleFriends

        override suspend fun getIncomingFriendRequests(): List<FriendRequestResponse> = emptyList()

        override suspend fun getOutgoingFriendRequests(): List<FriendRequestResponse> = emptyList()

        override suspend fun createFriendRequest(request: CreateFriendRequest): FriendRequestResponse =
            FriendRequestResponse(id = 1L, requester = sampleFriends[0], addressee = sampleFriends[1], status = "PENDING", message = "")

        override suspend fun acceptFriendRequest(requestId: Long): FriendRequestResponse =
            FriendRequestResponse(id = 1L, requester = sampleFriends[0], addressee = sampleFriends[1], status = "ACCEPTED", message = "")

        override suspend fun rejectFriendRequest(requestId: Long): FriendRequestResponse =
            FriendRequestResponse(id = 1L, requester = sampleFriends[0], addressee = sampleFriends[1], status = "REJECTED", message = "")
    }

    private lateinit var contactRepository: ContactRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        contactRepository = ContactRepository(fakeContactDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun awaitStateLoaded(viewModel: ContactViewModel) {
        var attempts = 0
        while (viewModel.uiState.value.isLoading && attempts < 40) {
            Thread.sleep(50)
            testDispatcher.scheduler.advanceUntilIdle()
            attempts++
        }
    }

    @Test
    fun testRefreshFriends_loadsAndGroupsAlphabetically() = runTest(testDispatcher) {
        val viewModel = ContactViewModel(contactRepository)
        awaitStateLoaded(viewModel)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.friends.size)

        // 验证首字母分组
        val grouped = state.groupedFriends
        assertTrue(grouped.containsKey("A"))
        assertTrue(grouped.containsKey("B"))
        assertTrue(grouped.containsKey("C"))
        assertEquals("Alice", grouped["A"]?.first()?.displayName)
    }

    @Test
    fun testUpdateSearchQuery_filtersLocalContacts() = runTest(testDispatcher) {
        val viewModel = ContactViewModel(contactRepository)
        awaitStateLoaded(viewModel)

        viewModel.updateSearchQuery("Ali")
        val state = viewModel.uiState.value
        assertEquals("Ali", state.searchQuery)
        assertEquals(1, state.displayedFriends.size)
        assertEquals("Alice", state.displayedFriends.first().displayName)

        // 清空搜索返回全量
        viewModel.updateSearchQuery("")
        assertEquals(3, viewModel.uiState.value.displayedFriends.size)
    }

    @Test
    fun testToggleSearch_andToggleSortOrder() = runTest(testDispatcher) {
        val viewModel = ContactViewModel(contactRepository)
        awaitStateLoaded(viewModel)

        assertFalse(viewModel.uiState.value.isSearchActive)
        viewModel.toggleSearch(true)
        assertTrue(viewModel.uiState.value.isSearchActive)

        assertFalse(viewModel.uiState.value.sortByOnline)
        viewModel.toggleSortOrder()
        assertTrue(viewModel.uiState.value.sortByOnline)
    }
}
