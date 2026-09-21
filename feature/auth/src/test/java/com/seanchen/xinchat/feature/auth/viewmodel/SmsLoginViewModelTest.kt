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
import com.seanchen.xinchat.core.network.datadource.auth.AuthNetworkDataSource
import com.seanchen.xinchat.core.network.datadource.userinfo.UserInfoNetworkDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class SmsLoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + testDispatcher)

    private val fakeAuthDataSource = object : AuthNetworkDataSource {
        override suspend fun register(params: Map<String, String>): LoginResponse =
            LoginResponse(id = 1L, username = "test", displayName = "Test", email = "test@test.com", avatarColor = 0, accessToken = "tok", expiresAt = "exp")

        override suspend fun getRegisterCode(params: Map<String, String>): NetworkResponse<String> =
            NetworkResponse(data = "1234")

        override suspend fun getPasswordCode(params: Map<String, String>): NetworkResponse<String> =
            NetworkResponse(data = "1234")

        override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Boolean> =
            NetworkResponse(data = true)

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

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = AuthRepository(fakeAuthDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        scope.cancel()
    }

    @Test
    fun testUpdatePhone_filtersNonDigits_andLimitsLength() {
        val fakeAppState = createFakeAppState(scope)
        val viewModel = SmsLoginViewModel(fakeAppState, authRepository)

        viewModel.updatePhone("138-0013-8000999")
        assertEquals("13800138000", viewModel.uiState.value.phone)
    }

    @Test
    fun testUpdateVerificationCode_filtersNonDigits_andLimits4Digits() {
        val fakeAppState = createFakeAppState(scope)
        val viewModel = SmsLoginViewModel(fakeAppState, authRepository)

        viewModel.updateVerificationCode("12a345")
        assertEquals("1234", viewModel.uiState.value.verificationCode)
    }

    @Test
    fun testCanSendCode_andCanLogin() {
        val fakeAppState = createFakeAppState(scope)
        val viewModel = SmsLoginViewModel(fakeAppState, authRepository)

        assertFalse(viewModel.uiState.value.canSendCode)
        assertFalse(viewModel.uiState.value.canLogin)

        viewModel.updatePhone("13800138000")
        assertTrue(viewModel.uiState.value.canSendCode)
        assertFalse(viewModel.uiState.value.canLogin)

        viewModel.updateVerificationCode("1234")
        assertTrue(viewModel.uiState.value.canLogin)
    }

    @Test
    fun testSendVerificationCode_startsCountdown() = runTest(testDispatcher) {
        val fakeAppState = createFakeAppState(scope)
        val viewModel = SmsLoginViewModel(fakeAppState, authRepository)

        viewModel.updatePhone("13800138000")
        viewModel.sendVerificationCode()

        advanceTimeBy(350)
        assertTrue(viewModel.uiState.value.verificationCode.isNotEmpty())
        assertEquals(60, viewModel.uiState.value.resendCountdown)
        assertFalse(viewModel.uiState.value.canSendCode)

        advanceTimeBy(1000)
        assertEquals(59, viewModel.uiState.value.resendCountdown)

        // 推进剩余倒计时时间使其正常结束
        advanceTimeBy(60_000)
        assertEquals(0, viewModel.uiState.value.resendCountdown)
        assertTrue(viewModel.uiState.value.canSendCode)
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
            override suspend fun getPersonInfo(): NetworkResponse<User> =
                NetworkResponse(data = User(id = 1, nickName = "test", phone = "13800138000"))
            override suspend fun updatePersonInfo(params: Map<String, Any>): NetworkResponse<Any> =
                NetworkResponse(data = true)
            override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Any> =
                NetworkResponse(data = true)
            override suspend fun logoff(params: Map<String, Any>): NetworkResponse<Boolean> =
                NetworkResponse(data = true)
            override suspend fun bindPhone(params: Map<String, String>): NetworkResponse<Any> =
                NetworkResponse(data = true)
        }

        return AppState(
            AuthStoreRepository(fakeAuthStoreDataSource),
            UserInfoStoreRepository(fakeUserInfoStoreDataSource),
            UserInfoRepository(fakeUserInfoNetworkDataSource),
            coroutineScope
        )
    }
}
