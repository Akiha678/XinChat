package com.seanchen.xinchat.feature.auth.viewmodel

import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.data.repository.AuthStoreRepository
import com.seanchen.xinchat.core.data.repository.UserInfoRepository
import com.seanchen.xinchat.core.data.repository.UserInfoStoreRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.datastore.datasource.auth.AuthStoreDataSource
import com.seanchen.xinchat.core.datastore.datasource.userinfo.UserInfoStoreDataSource
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.LoginResponse
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.navigation.AppNavigator
import com.seanchen.xinchat.core.navigation.NavigationService
import com.seanchen.xinchat.core.network.datadource.auth.AuthNetworkDataSource
import com.seanchen.xinchat.core.network.datadource.userinfo.UserInfoNetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountLoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var scope: CoroutineScope

    private val fakeAuthDataSource = object : AuthNetworkDataSource {
        override suspend fun register(params: Map<String, String>): LoginResponse =
            LoginResponse(id = 1L, username = "test", displayName = "Test", email = "test@test.com", avatarColor = 0, accessToken = "tok", expiresAt = "exp")

        override suspend fun getRegisterCode(params: Map<String, String>): NetworkResponse<String> =
            NetworkResponse(data = "1234")

        override suspend fun getPasswordCode(params: Map<String, String>): NetworkResponse<String> =
            NetworkResponse(data = "1234")

        override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Boolean> =
            NetworkResponse(data = true)

        override suspend fun getSmsCode(params: Map<String, String>): NetworkResponse<String> =
            NetworkResponse(data = "1234")

        override suspend fun loginByPhone(params: Map<String, String>): NetworkResponse<Auth> =
            NetworkResponse(data = Auth("tok", "ref", 100, 100, 0))

        override suspend fun loginByPassword(params: Map<String, String>): NetworkResponse<Auth> =
            NetworkResponse(data = Auth("tok", "ref", 100, 100, 0))

        override suspend fun verifyCaptcha(params: Map<String, String>): NetworkResponse<Boolean> =
            NetworkResponse(data = true)

        override suspend fun getCaptcha(): NetworkResponse<Captcha> =
            NetworkResponse(data = Captcha("img", "id"))

        override suspend fun refreshToken(params: Map<String, String>): NetworkResponse<Auth> =
            NetworkResponse(data = Auth("tok", "ref", 100, 100, 0))
    }

    private lateinit var authRepository: AuthRepository
    private lateinit var appNavigator: AppNavigator
    private lateinit var fakeAppState: AppState

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        scope = CoroutineScope(SupervisorJob() + testDispatcher)
        fakeAppState = createFakeAppState(scope)
        appNavigator = AppNavigator(fakeAppState, scope)
        NavigationService.bind(appNavigator)
        authRepository = AuthRepository(fakeAuthDataSource)
    }

    @After
    fun tearDown() {
        NavigationService.unbind(appNavigator)
        scope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun testUpdateAccountAndPassword() {
        val viewModel = AccountLoginViewModel(fakeAppState, authRepository)

        viewModel.updateAccount("user@example.com")
        viewModel.updatePassword("password123")

        assertEquals("user@example.com", viewModel.account.value)
        assertEquals("password123", viewModel.password.value)
    }

    @Test
    fun testInitAccount() {
        val viewModel = AccountLoginViewModel(fakeAppState, authRepository)

        viewModel.initAccount("user@example.com")
        assertEquals("user@example.com", viewModel.account.value)

        // 再次传入空串不覆盖
        viewModel.initAccount("")
        assertEquals("user@example.com", viewModel.account.value)
    }

    @Test
    fun testIsLoginEnabled() = runTest(testDispatcher) {
        val viewModel = AccountLoginViewModel(fakeAppState, authRepository)

        assertFalse(viewModel.isLoginEnabled.first())

        // 格式非法
        viewModel.updateAccount("not-an-email")
        viewModel.updatePassword("123456")
        assertFalse(viewModel.isLoginEnabled.first())

        // 密码少于6位
        viewModel.updateAccount("user@example.com")
        viewModel.updatePassword("12345")
        assertFalse(viewModel.isLoginEnabled.first())

        // 邮箱与密码均合法
        viewModel.updatePassword("123456")
        assertTrue(viewModel.isLoginEnabled.first())
    }

    @Test
    fun testLogin_success() = runTest(testDispatcher) {
        val viewModel = AccountLoginViewModel(fakeAppState, authRepository)

        viewModel.updateAccount("user@example.com")
        viewModel.updatePassword("password123")

        var callbackTriggered = false
        viewModel.login(onSuccess = {
            callbackTriggered = true
        })

        awaitCondition { callbackTriggered && fakeAppState.isLoggedIn.value }
        assertTrue(callbackTriggered)
        assertTrue(fakeAppState.isLoggedIn.value)
        assertEquals("tok", fakeAppState.auth.value?.token)

        testScheduler.advanceUntilIdle()
    }

    private fun awaitCondition(timeoutMs: Long = 2000, condition: () -> Boolean) {
        val start = System.currentTimeMillis()
        while (!condition() && System.currentTimeMillis() - start < timeoutMs) {
            Thread.sleep(20)
            testDispatcher.scheduler.runCurrent()
        }
    }

    private fun createFakeAppState(coroutineScope: CoroutineScope): AppState {
        val fakeAuthStoreDataSource = object : AuthStoreDataSource {
            private var auth: Auth? = null
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

        val fakeUserInfoNetworkDataSource = object : UserInfoNetworkDataSource {
            override suspend fun uploadAvatar(file: MultipartBody.Part): NetworkResponse<User> =
                NetworkResponse(data = User(id = 1, nickName = "test", avatarUrl = "/uploads/test.png"))
            override suspend fun updateNickname(request: com.seanchen.xinchat.core.model.request.UpdateNicknameRequest): NetworkResponse<User> =
                NetworkResponse(data = User(id = 1, nickName = request.nickName))
            override suspend fun getPersonInfo(): NetworkResponse<User> =
                NetworkResponse(data = User(id = 1, nickName = "test", phone = "13800138000"))
            override suspend fun updatePersonInfo(params: Map<String, Any>): NetworkResponse<Any> =
                NetworkResponse(data = true)
            override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Any> =
                NetworkResponse(data = true)
            override suspend fun logoff(params: Map<String, Any>): NetworkResponse<Boolean> =
                NetworkResponse(data = true)
            override suspend fun bindPhone(params: Map<String, String>): NetworkResponse<User> =
                NetworkResponse(data = User(id = 1, nickName = "test"))
        }

        return AppState(
            AuthStoreRepository(fakeAuthStoreDataSource),
            UserInfoStoreRepository(fakeUserInfoStoreDataSource),
            UserInfoRepository(fakeUserInfoNetworkDataSource),
            coroutineScope
        )
    }
}
