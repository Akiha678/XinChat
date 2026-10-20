package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.datastore.datasource.auth.AuthStoreDataSource
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.response.LoginResponse
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.datadource.auth.AuthNetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * [AuthRepository] 单元测试
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeNetworkDataSource = object : AuthNetworkDataSource {
        var lastRegisterParams: Map<String, String>? = null
        var lastLoginPhoneParams: Map<String, String>? = null
        var lastLoginPasswordParams: Map<String, String>? = null
        var lastSmsCodeParams: Map<String, String>? = null
        var lastVerifyCaptchaParams: Map<String, String>? = null
        var lastUpdatePasswordParams: Map<String, String>? = null
        var lastRefreshTokenParams: Map<String, String>? = null

        override suspend fun register(params: Map<String, String>): LoginResponse {
            lastRegisterParams = params
            return LoginResponse(
                id = 1001L,
                username = params["username"].orEmpty(),
                displayName = "Tester",
                email = params["email"].orEmpty(),
                accessToken = "access_token_123"
            )
        }

        override suspend fun getRegisterCode(params: Map<String, String>): NetworkResponse<String> =
            NetworkResponse(code = 200, message = "ok", data = "1111")

        override suspend fun getPasswordCode(params: Map<String, String>): NetworkResponse<String> =
            NetworkResponse(code = 200, message = "ok", data = "2222")

        override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Boolean> {
            lastUpdatePasswordParams = params
            return NetworkResponse(code = 200, message = "ok", data = true)
        }

        override suspend fun getSmsCode(params: Map<String, String>): NetworkResponse<String> {
            lastSmsCodeParams = params
            return NetworkResponse(code = 200, message = "ok", data = "3333")
        }

        override suspend fun loginByPhone(params: Map<String, String>): NetworkResponse<Auth> {
            lastLoginPhoneParams = params
            return NetworkResponse(
                code = 200,
                message = "ok",
                data = Auth(token = "phone_tok", refreshToken = "ref_tok", expire = 3600, refreshExpire = 7200, createdAt = 0)
            )
        }

        override suspend fun loginByPassword(params: Map<String, String>): NetworkResponse<Auth> {
            lastLoginPasswordParams = params
            return NetworkResponse(
                code = 200,
                message = "ok",
                data = Auth(token = "pwd_tok", refreshToken = "ref_tok", expire = 3600, refreshExpire = 7200, createdAt = 0)
            )
        }

        override suspend fun verifyCaptcha(params: Map<String, String>): NetworkResponse<Boolean> {
            lastVerifyCaptchaParams = params
            return NetworkResponse(code = 200, message = "ok", data = true)
        }

        override suspend fun getCaptcha(): NetworkResponse<Captcha> =
            NetworkResponse(code = 200, message = "ok", data = Captcha(data = "image_base64", captchaId = "captcha_id_999"))

        override suspend fun refreshToken(params: Map<String, String>): NetworkResponse<Auth> {
            lastRefreshTokenParams = params
            return NetworkResponse(
                code = 200,
                message = "ok",
                data = Auth(token = "new_tok", refreshToken = "new_ref", expire = 3600, refreshExpire = 7200, createdAt = 0)
            )
        }
    }

    private val fakeStoreDataSource = object : AuthStoreDataSource {
        private var storedAuth: Auth? = null

        override suspend fun saveAuth(auth: Auth) {
            storedAuth = auth
        }

        override suspend fun getAuth(): Auth? = storedAuth

        override suspend fun getToken(): String? = storedAuth?.token

        override suspend fun clearAuth() {
            storedAuth = null
        }

        override suspend fun isLoggedIn(): Boolean = storedAuth != null
    }

    private lateinit var authStoreRepository: AuthStoreRepository
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        authStoreRepository = AuthStoreRepository(fakeStoreDataSource)
        authRepository = AuthRepository(
            authNetworkDataSource = fakeNetworkDataSource,
            authStoreRepository = authStoreRepository,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun testSendVerificationCode_and_getSmsCode() = runTest(testDispatcher) {
        val result = authRepository.sendVerificationCode("user@example.com").first()
        assertEquals(200, result.code)
        assertEquals("3333", result.data)
        assertEquals("user@example.com", fakeNetworkDataSource.lastSmsCodeParams?.get("email"))
        assertEquals("user@example.com", fakeNetworkDataSource.lastSmsCodeParams?.get("account"))
    }

    @Test
    fun testLoginByCode_delegatesCorrectly() = runTest(testDispatcher) {
        val result = authRepository.loginByCode("user@example.com", "1234").first()
        assertEquals(200, result.code)
        assertEquals("phone_tok", result.data?.token)
        assertEquals("1234", fakeNetworkDataSource.lastLoginPhoneParams?.get("code"))
        assertEquals("user@example.com", fakeNetworkDataSource.lastLoginPhoneParams?.get("email"))
    }

    @Test
    fun testLoginByPassword_delegatesCorrectly() = runTest(testDispatcher) {
        val result = authRepository.loginByPassword("user@example.com", "secret123").first()
        assertEquals(200, result.code)
        assertEquals("pwd_tok", result.data?.token)
        assertEquals("secret123", fakeNetworkDataSource.lastLoginPasswordParams?.get("password"))
        assertEquals("user@example.com", fakeNetworkDataSource.lastLoginPasswordParams?.get("account"))
    }

    @Test
    fun testRegister_and_registerFlow() = runTest(testDispatcher) {
        val flowResult = authRepository.registerFlow("alice", "pwd", "alice@test.com", "6666").first()
        assertEquals(1001L, flowResult.id)
        assertEquals("alice", flowResult.username)
        assertEquals("alice@test.com", fakeNetworkDataSource.lastRegisterParams?.get("email"))

        val suspendResult = authRepository.register(mapOf("username" to "bob"))
        assertEquals(1001L, suspendResult.id)
        assertEquals("bob", suspendResult.username)
    }

    @Test
    fun testUpdatePassword_delegatesCorrectly() = runTest(testDispatcher) {
        val result = authRepository.updatePassword("user@example.com", "new_pwd", "8888").first()
        assertEquals(200, result.code)
        assertTrue(result.data == true)
        assertEquals("new_pwd", fakeNetworkDataSource.lastUpdatePasswordParams?.get("password"))
        assertEquals("8888", fakeNetworkDataSource.lastUpdatePasswordParams?.get("code"))
    }

    @Test
    fun testCaptchaOperations() = runTest(testDispatcher) {
        val captcha = authRepository.getCaptcha().first()
        assertEquals("captcha_id_999", captcha.data?.captchaId)

        val verify = authRepository.verifyCaptcha("captcha_id_999", "ABCD").first()
        assertTrue(verify.data == true)
        assertEquals("captcha_id_999", fakeNetworkDataSource.lastVerifyCaptchaParams?.get("id"))
        assertEquals("ABCD", fakeNetworkDataSource.lastVerifyCaptchaParams?.get("code"))
    }

    @Test
    fun testRefreshToken_delegatesCorrectly() = runTest(testDispatcher) {
        val result = authRepository.refreshToken("old_refresh_token").first()
        assertEquals(200, result.code)
        assertEquals("new_tok", result.data?.token)
        assertEquals("old_refresh_token", fakeNetworkDataSource.lastRefreshTokenParams?.get("refreshToken"))
    }

    @Test
    fun testLocalSessionStateCooperation() = runTest(testDispatcher) {
        assertFalse(authRepository.isLoggedIn())
        assertNull(authRepository.getCurrentToken())
        assertNull(authRepository.getCurrentAuth())

        val testAuth = Auth(token = "tok_abc", refreshToken = "ref_abc", expire = 3600, refreshExpire = 7200, createdAt = 0)
        authRepository.saveAuth(testAuth)

        assertTrue(authRepository.isLoggedIn())
        assertEquals("tok_abc", authRepository.getCurrentToken())
        assertEquals(testAuth, authRepository.getCurrentAuth())

        authRepository.clearAuth()
        assertFalse(authRepository.isLoggedIn())
        assertNull(authRepository.getCurrentToken())
    }

    @Test
    fun testNullStoreRepositoryFallback() = runTest(testDispatcher) {
        val repoWithoutStore = AuthRepository(fakeNetworkDataSource)
        assertFalse(repoWithoutStore.isLoggedIn())
        assertNull(repoWithoutStore.getCurrentToken())
        assertNull(repoWithoutStore.getCurrentAuth())
    }
}
