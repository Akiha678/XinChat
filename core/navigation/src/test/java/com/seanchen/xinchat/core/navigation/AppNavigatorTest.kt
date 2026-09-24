package com.seanchen.xinchat.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.seanchen.xinchat.core.data.repository.AuthStoreRepository
import com.seanchen.xinchat.core.data.repository.UserInfoRepository
import com.seanchen.xinchat.core.data.repository.UserInfoStoreRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.datastore.datasource.auth.AuthStoreDataSource
import com.seanchen.xinchat.core.datastore.datasource.userinfo.UserInfoStoreDataSource
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.core.navigation.user.UserRoutes
import com.seanchen.xinchat.core.network.datadource.userinfo.UserInfoNetworkDataSource
import com.seanchen.xinchat.core.result.SessionExpiryNotifier
import okhttp3.MultipartBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

/**
 * 导航管理器测试
 *
 * 覆盖命令队列、登录拦截与登录态失效后的返回栈替换。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppNavigatorTest {

    private val applicationScope = CoroutineScope(UnconfinedTestDispatcher() + SupervisorJob())

    private lateinit var appState: AppState
    private lateinit var navigator: AppNavigator
    private lateinit var backStack: NavBackStack<NavKey>

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        appState = AppState(
            authStoreRepository = AuthStoreRepository(FakeAuthStoreDataSource()),
            userInfoStoreRepository = UserInfoStoreRepository(FakeUserInfoStoreDataSource()),
            userInfoRepository = UserInfoRepository(FakeUserInfoNetworkDataSource()),
            applicationScope = applicationScope
        )
        navigator = AppNavigator(appState = appState, applicationScope = applicationScope)
        backStack = NavBackStack(MainRoutes.Main)
    }

    @After
    fun tearDown() {
        applicationScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `navigateTo queues command until controller attached`() {
        navigator.navigateTo(AuthRoutes.Register)

        assertEquals(listOf<NavKey>(MainRoutes.Main), backStack.toList())

        attachController()

        assertEquals(listOf<NavKey>(MainRoutes.Main, AuthRoutes.Register), backStack.toList())
    }

    @Test
    fun `navigateTo login required route without session redirects to login`() {
        attachController()

        navigator.navigateTo(UserRoutes.Profile)

        assertEquals(listOf<NavKey>(MainRoutes.Main, AuthRoutes.Login), backStack.toList())
    }

    @Test
    fun `navigateTo login required route with session keeps target route`() {
        login()
        attachController()

        navigator.navigateTo(UserRoutes.Profile)

        assertEquals(listOf<NavKey>(MainRoutes.Main, UserRoutes.Profile), backStack.toList())
    }

    @Test
    fun `logout clears back stack and shows login`() {
        login()
        attachController()
        navigator.navigateTo(UserRoutes.Profile)

        runBlocking { appState.logout() }

        assertFalse(appState.isLoggedIn.value)
        assertEquals(listOf<NavKey>(AuthRoutes.Login), backStack.toList())
    }

    @Test
    fun `expired session clears back stack and shows login`() {
        login()
        attachController()
        navigator.navigateTo(UserRoutes.Profile)

        SessionExpiryNotifier.notifyExpired()

        assertFalse(appState.isLoggedIn.value)
        assertEquals(listOf<NavKey>(AuthRoutes.Login), backStack.toList())
    }

    @Test
    fun `expired session keeps session when app never logged in`() {
        attachController()

        SessionExpiryNotifier.notifyExpired()

        assertFalse(appState.isLoggedIn.value)
        assertEquals(listOf<NavKey>(MainRoutes.Main), backStack.toList())
    }

    @Test
    fun `navigateBack keeps root destination`() {
        attachController()

        navigator.navigateBack()

        assertEquals(listOf<NavKey>(MainRoutes.Main), backStack.toList())
    }

    @Test
    fun `navigateBackTo removes destinations after target`() {
        login()
        attachController()
        navigator.navigateTo(UserRoutes.Profile)

        navigator.navigateBackTo(MainRoutes.Main)

        assertEquals(listOf<NavKey>(MainRoutes.Main), backStack.toList())
    }

    /**
     * 写入登录态，使 AppState 处于已登录状态
     */
    private fun login() = runBlocking {
        appState.updateAuth(Auth(token = "test_token"))
        appState.updateUserInfo(User(id = 1, nickName = "tester"))
    }

    private fun attachController() {
        navigator.attachController(
            createBackStackNavigationController(backStack = backStack, navigator = navigator)
        )
    }
}

private class FakeAuthStoreDataSource : AuthStoreDataSource {
    private var auth: Auth? = null

    override suspend fun saveAuth(auth: Auth) {
        this.auth = auth
    }

    override suspend fun getAuth(): Auth? = auth

    override suspend fun getToken(): String? = auth?.token

    override suspend fun clearAuth() {
        auth = null
    }

    override suspend fun isLoggedIn(): Boolean = auth != null
}

private class FakeUserInfoStoreDataSource : UserInfoStoreDataSource {
    private var user: User? = null

    override suspend fun saveUserInfo(user: User) {
        this.user = user
    }

    override suspend fun getUserInfo(): User? = user

    override suspend fun updateUserInfo(updates: Map<String, Any?>) = Unit

    override suspend fun clearUserInfo() {
        user = null
    }

    override suspend fun getUserId(): Long = user?.id ?: 0L

    override suspend fun getNickName(): String? = user?.nickName

    override suspend fun getAvatarUrl(): String? = user?.avatarUrl
}

private class FakeUserInfoNetworkDataSource : UserInfoNetworkDataSource {
    override suspend fun uploadAvatar(file: MultipartBody.Part): NetworkResponse<User> {
        return NetworkResponse()
    }

    override suspend fun updateNickname(request: com.seanchen.xinchat.core.model.request.UpdateNicknameRequest): NetworkResponse<User> {
        return NetworkResponse()
    }

    override suspend fun updatePersonInfo(params: Map<String, Any>): NetworkResponse<Any> {
        return NetworkResponse()
    }

    override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Any> {
        return NetworkResponse()
    }

    override suspend fun logoff(params: Map<String, Any>): NetworkResponse<Boolean> {
        return NetworkResponse()
    }

    override suspend fun bindPhone(params: Map<String, String>): NetworkResponse<Any> {
        return NetworkResponse()
    }

    override suspend fun getPersonInfo(): NetworkResponse<User> {
        return NetworkResponse()
    }
}
