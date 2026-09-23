package com.seanchen.xinchat.feature.user.viewmodel

import com.seanchen.xinchat.core.data.repository.AuthStoreRepository
import com.seanchen.xinchat.core.data.repository.UserInfoRepository
import com.seanchen.xinchat.core.data.repository.UserInfoStoreRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.datastore.datasource.auth.AuthStoreDataSource
import com.seanchen.xinchat.core.datastore.datasource.userinfo.UserInfoStoreDataSource
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.datadource.userinfo.UserInfoNetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var scope: CoroutineScope
    private lateinit var fakeAppState: AppState
    private lateinit var fakeUserInfoNetworkDataSource: FakeUserInfoNetworkDataSource
    private lateinit var userInfoRepository: UserInfoRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        scope = CoroutineScope(SupervisorJob() + testDispatcher)

        fakeUserInfoNetworkDataSource = FakeUserInfoNetworkDataSource()
        userInfoRepository = UserInfoRepository(fakeUserInfoNetworkDataSource)
        fakeAppState = createFakeAppState(scope, userInfoRepository)
    }

    @After
    fun tearDown() {
        scope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun uploadAvatar_success_updatesUserInfo_andCallsSuccessCallback() = runTest(testDispatcher) {
        val initialUser = User(id = 1, nickName = "OldName", avatarUrl = "/old/avatar.jpg")
        fakeAppState.updateUserInfo(initialUser)
        testScheduler.runCurrent()

        val expectedUser = User(id = 1, nickName = "OldName", avatarUrl = "/uploads/20260923/new_avatar.png")
        fakeUserInfoNetworkDataSource.uploadAvatarResult = NetworkResponse(code = 1000, message = "success", data = expectedUser)

        val viewModel = ProfileViewModel(fakeAppState, userInfoRepository)

        val dummyPart = MultipartBody.Part.createFormData("file", "test.png", "dummy".toRequestBody("image/png".toMediaTypeOrNull()))

        var isSuccessCalled = false
        var isErrorCalled = false

        viewModel.uploadAvatar(
            part = dummyPart,
            onSuccess = { isSuccessCalled = true },
            onError = { isErrorCalled = true }
        )

        assertTrue(viewModel.isUploadingAvatar.value)

        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.isUploadingAvatar.value)
        assertTrue(isSuccessCalled)
        assertFalse(isErrorCalled)
        assertEquals("/uploads/20260923/new_avatar.png", fakeAppState.userInfo.value?.avatarUrl)
    }

    @Test
    fun uploadAvatar_failure_doesNotUpdateUserInfo_andCallsErrorCallback() = runTest(testDispatcher) {
        val initialUser = User(id = 1, nickName = "OldName", avatarUrl = "/old/avatar.jpg")
        fakeAppState.updateUserInfo(initialUser)
        testScheduler.runCurrent()

        fakeUserInfoNetworkDataSource.uploadAvatarResult = NetworkResponse(code = 400, message = "仅支持指定图片格式", data = null)

        val viewModel = ProfileViewModel(fakeAppState, userInfoRepository)

        val dummyPart = MultipartBody.Part.createFormData("file", "bad.bmp", "dummy".toRequestBody("image/bmp".toMediaTypeOrNull()))

        var isSuccessCalled = false
        var errorMessage: String? = null

        viewModel.uploadAvatar(
            part = dummyPart,
            onSuccess = { isSuccessCalled = true },
            onError = { msg -> errorMessage = msg }
        )

        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.isUploadingAvatar.value)
        assertFalse(isSuccessCalled)
        assertEquals("仅支持指定图片格式", errorMessage)
        assertEquals("/old/avatar.jpg", fakeAppState.userInfo.value?.avatarUrl)
    }

    private class FakeUserInfoNetworkDataSource : UserInfoNetworkDataSource {
        var uploadAvatarResult: NetworkResponse<User> = NetworkResponse()

        override suspend fun uploadAvatar(file: MultipartBody.Part): NetworkResponse<User> = uploadAvatarResult
        override suspend fun getPersonInfo(): NetworkResponse<User> = NetworkResponse(data = User(id = 1, nickName = "test"))
        override suspend fun updatePersonInfo(params: Map<String, Any>): NetworkResponse<Any> = NetworkResponse(data = true)
        override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Any> = NetworkResponse(data = true)
        override suspend fun logoff(params: Map<String, Any>): NetworkResponse<Boolean> = NetworkResponse(data = true)
        override suspend fun bindPhone(params: Map<String, String>): NetworkResponse<Any> = NetworkResponse(data = true)
    }

    private fun createFakeAppState(
        coroutineScope: CoroutineScope,
        userInfoRepository: UserInfoRepository
    ): AppState {
        val fakeAuthStoreDataSource = object : AuthStoreDataSource {
            private var auth: Auth? = Auth("token")
            override suspend fun saveAuth(auth: Auth) { this.auth = auth }
            override suspend fun getAuth(): Auth? = auth
            override suspend fun getToken(): String? = auth?.token
            override suspend fun clearAuth() { auth = null }
            override suspend fun isLoggedIn(): Boolean = auth != null
        }

        val fakeUserInfoStoreDataSource = object : UserInfoStoreDataSource {
            private var user: User? = null
            override suspend fun saveUserInfo(user: User) { this.user = user }
            override suspend fun getUserInfo(): User? = user
            override suspend fun updateUserInfo(updates: Map<String, Any?>) = Unit
            override suspend fun clearUserInfo() { user = null }
            override suspend fun getUserId(): Long = user?.id ?: 0L
            override suspend fun getNickName(): String? = user?.nickName
            override suspend fun getAvatarUrl(): String? = user?.avatarUrl
        }

        return AppState(
            AuthStoreRepository(fakeAuthStoreDataSource),
            UserInfoStoreRepository(fakeUserInfoStoreDataSource),
            userInfoRepository,
            coroutineScope
        )
    }
}
