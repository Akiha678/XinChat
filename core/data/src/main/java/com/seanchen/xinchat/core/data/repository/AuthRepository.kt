package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.Captcha
import com.seanchen.xinchat.core.model.response.LoginResponse
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.datadource.auth.AuthNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证与授权仓储（管理邮箱登录、密码登录、注册及本地凭据）
 *
 * @param authNetworkDataSource 认证网络数据源
 * @param authStoreRepository 本地认证存储仓储（可选）
 * @param ioDispatcher 协程 I/O 调度器（默认 [Dispatchers.IO]）
 */
@Singleton
class AuthRepository(
    private val authNetworkDataSource: AuthNetworkDataSource,
    private val authStoreRepository: AuthStoreRepository? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    @Inject
    constructor(
        authNetworkDataSource: AuthNetworkDataSource,
        authStoreRepository: AuthStoreRepository
    ) : this(authNetworkDataSource, authStoreRepository, Dispatchers.IO)

    // ==================================================================================
    // 验证码操作
    // ==================================================================================

    /**
     * 发送邮箱登录验证码（便捷强类型方法）
     *
     * @param email 目标邮箱
     */
    fun sendVerificationCode(email: String): Flow<NetworkResponse<String>> =
        getEmailCode(email)

    /**
     * 获取邮箱登录验证码
     *
     * @param email 目标邮箱
     */
    fun getEmailCode(email: String): Flow<NetworkResponse<String>> =
        getEmailCode(mapOf("email" to email))

    /**
     * 获取邮箱登录验证码（Map 入参）
     */
    fun getEmailCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getEmailCode(params))
    }.flowOn(ioDispatcher)

    /**
     * 获取注册验证码
     *
     * @param email 注册邮箱
     */
    fun getRegisterCode(email: String): Flow<NetworkResponse<String>> =
        getRegisterCode(mapOf("email" to email))

    /**
     * 获取注册验证码（Map 入参）
     */
    fun getRegisterCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getRegisterCode(params))
    }.flowOn(ioDispatcher)

    /**
     * 获取重置密码验证码
     *
     * @param email 目标邮箱
     */
    fun getPasswordCode(email: String): Flow<NetworkResponse<String>> =
        getPasswordCode(mapOf("email" to email))

    /**
     * 获取修改密码验证码（Map 入参）
     */
    fun getPasswordCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getPasswordCode(params))
    }.flowOn(ioDispatcher)

    // ==================================================================================
    // 登录与注册操作
    // ==================================================================================

    /**
     * 邮箱验证码登录（未注册自动创建账号并返回Token，登录即注册）
     *
     * @param email 邮箱地址
     * @param code 4 位纯数字验证码
     */
    fun loginByEmail(email: String, code: String): Flow<NetworkResponse<Auth>> =
        loginByEmail(mapOf("email" to email, "code" to code))

    /**
     * 邮箱验证码登录（Map 入参）
     */
    fun loginByEmail(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.loginByEmail(params))
    }.flowOn(ioDispatcher)

    /**
     * 邮箱密码登录
     *
     * @param email 邮箱地址
     * @param password 明文密码
     */
    fun loginByPassword(email: String, password: String): Flow<NetworkResponse<Auth>> =
        loginByPassword(mapOf("email" to email, "password" to password))

    /**
     * 邮箱密码登录（Map 入参）
     */
    fun loginByPassword(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.loginByPassword(params))
    }.flowOn(ioDispatcher)

    /**
     * 用户注册
     */
    fun registerFlow(
        username: String,
        password: String,
        email: String,
        code: String
    ): Flow<LoginResponse> = flow {
        emit(register(mapOf("username" to username, "password" to password, "email" to email, "code" to code)))
    }.flowOn(ioDispatcher)

    /**
     * 用户注册（挂起函数）
     */
    suspend fun register(params: Map<String, String>): LoginResponse {
        return authNetworkDataSource.register(params)
    }

    /**
     * 修改密码
     *
     * @param email 邮箱地址
     * @param newPassword 新密码
     * @param code 邮箱验证码
     */
    fun updatePassword(
        email: String,
        newPassword: String,
        code: String
    ): Flow<NetworkResponse<Boolean>> = updatePassword(
        mapOf(
            "email" to email,
            "newPassword" to newPassword,
            "code" to code
        )
    )

    /**
     * 修改密码（Map 入参）
     */
    fun updatePassword(params: Map<String, String>): Flow<NetworkResponse<Boolean>> = flow {
        emit(authNetworkDataSource.updatePassword(params))
    }.flowOn(ioDispatcher)

    // ==================================================================================
    // 图形验证码防人机
    // ==================================================================================

    /**
     * 获取图片验证码
     */
    fun getCaptcha(): Flow<NetworkResponse<Captcha>> = flow {
        emit(authNetworkDataSource.getCaptcha())
    }.flowOn(ioDispatcher)

    /**
     * 校验图形验证码
     *
     * @param captchaId 验证码标识
     * @param captchaCode 用户输入的验证码文字
     */
    fun verifyCaptcha(captchaId: String, captchaCode: String): Flow<NetworkResponse<Boolean>> =
        verifyCaptcha(mapOf("captchaId" to captchaId, "code" to captchaCode))

    /**
     * 校验图形验证码（Map 入参）
     */
    fun verifyCaptcha(params: Map<String, String>): Flow<NetworkResponse<Boolean>> = flow {
        emit(authNetworkDataSource.verifyCaptcha(params))
    }.flowOn(ioDispatcher)

    // ==================================================================================
    // 令牌与本地会话管理
    // ==================================================================================

    /**
     * 刷新 Token
     */
    fun refreshToken(refreshToken: String): Flow<NetworkResponse<Auth>> =
        refreshToken(mapOf("refreshToken" to refreshToken))

    /**
     * 刷新 Token（Map 入参）
     */
    fun refreshToken(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.refreshToken(params))
    }.flowOn(ioDispatcher)

    /**
     * 检查用户是否已登录本地持久态
     */
    suspend fun isLoggedIn(): Boolean = authStoreRepository?.isLoggedIn() ?: false

    /**
     * 获取本地持久化的认证信息
     */
    suspend fun getCurrentAuth(): Auth? = authStoreRepository?.getAuth()

    /**
     * 获取本地持久化的 AccessToken
     */
    suspend fun getCurrentToken(): String? = authStoreRepository?.getToken()

    /**
     * 保存认证信息到本地
     */
    suspend fun saveAuth(auth: Auth) {
        authStoreRepository?.saveAuth(auth)
    }

    /**
     * 清除本地认证信息（退出登录）
     */
    suspend fun clearAuth() {
        authStoreRepository?.clearAuth()
    }

    /**
     * 检查当前 Token 是否需要刷新
     */
    suspend fun shouldRefreshToken(): Boolean = authStoreRepository?.shouldRefreshToken() ?: false
}
