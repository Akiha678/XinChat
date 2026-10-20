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
 * 认证与授权仓储（统一管理远程认证网络接口与本地会话凭证状态）
 *
 * @param authNetworkDataSource 认证网络数据源
 * @param authStoreRepository 本地认证存储仓储（可选，提供本地 Token 与登录态协同）
 * @param ioDispatcher 协程 I/O 调度器（默认 [Dispatchers.IO]，测试时可注入）
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
    // 验证码相关方法
    // ==================================================================================

    /**
     * 发送登录验证码（便捷强类型方法）
     *
     * @param account 邮箱、手机号或账号标识
     */
    fun sendVerificationCode(account: String): Flow<NetworkResponse<String>> =
        getSmsCode(account)

    /**
     * 获取手机/邮箱登录验证码（便捷强类型方法）
     *
     * @param account 邮箱或手机号
     */
    fun getSmsCode(account: String): Flow<NetworkResponse<String>> =
        getSmsCode(mapOf("email" to account, "phone" to account, "account" to account))

    /**
     * 获取手机/邮箱登录验证码（通用 Map 入参版本，保留向下兼容）
     */
    fun getSmsCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getSmsCode(params))
    }.flowOn(ioDispatcher)

    /**
     * 获取注册验证码（便捷强类型方法）
     *
     * @param account 注册账号或邮箱
     */
    fun getRegisterCode(account: String): Flow<NetworkResponse<String>> =
        getRegisterCode(mapOf("account" to account, "email" to account, "phone" to account))

    /**
     * 获取注册验证码（通用 Map 入参版本，保留向下兼容）
     */
    fun getRegisterCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getRegisterCode(params))
    }.flowOn(ioDispatcher)

    /**
     * 获取重置密码验证码（便捷强类型方法）
     *
     * @param account 账号或邮箱
     */
    fun getPasswordCode(account: String): Flow<NetworkResponse<String>> =
        getPasswordCode(mapOf("account" to account, "email" to account, "phone" to account))

    /**
     * 获取修改密码验证码（通用 Map 入参版本，保留向下兼容）
     */
    fun getPasswordCode(params: Map<String, String>): Flow<NetworkResponse<String>> = flow {
        emit(authNetworkDataSource.getPasswordCode(params))
    }.flowOn(ioDispatcher)

    // ==================================================================================
    // 登录与注册操作
    // ==================================================================================

    /**
     * 验证码登录（便捷强类型方法，支持手机号与邮箱）
     *
     * @param account 邮箱或手机号
     * @param code 4-6 位纯数字验证码
     */
    fun loginByCode(account: String, code: String): Flow<NetworkResponse<Auth>> =
        loginByPhone(
            mapOf(
                "email" to account,
                "phone" to account,
                "account" to account,
                "code" to code
            )
        )

    /**
     * 手机号/验证码登录（通用 Map 入参版本，保留向下兼容）
     */
    fun loginByPhone(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.loginByPhone(params))
    }.flowOn(ioDispatcher)

    /**
     * 账号密码登录（便捷强类型方法）
     *
     * @param account 账号或邮箱
     * @param password 明文密码
     */
    fun loginByPassword(account: String, password: String): Flow<NetworkResponse<Auth>> =
        loginByPassword(
            mapOf(
                "account" to account,
                "email" to account,
                "password" to password
            )
        )

    /**
     * 账号密码登录（通用 Map 入参版本，保留向下兼容）
     */
    fun loginByPassword(params: Map<String, String>): Flow<NetworkResponse<Auth>> = flow {
        emit(authNetworkDataSource.loginByPassword(params))
    }.flowOn(ioDispatcher)

    /**
     * 用户注册（便捷 Flow 异步流方法）
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
     * 用户注册（挂起函数版本，保留向下兼容）
     */
    suspend fun register(params: Map<String, String>): LoginResponse {
        return authNetworkDataSource.register(params)
    }

    /**
     * 修改密码（便捷强类型方法）
     *
     * @param account 账号或邮箱
     * @param newPassword 新密码
     * @param code 校验通过的验证码
     */
    fun updatePassword(
        account: String,
        newPassword: String,
        code: String
    ): Flow<NetworkResponse<Boolean>> = updatePassword(
        mapOf(
            "account" to account,
            "email" to account,
            "password" to newPassword,
            "code" to code
        )
    )

    /**
     * 修改密码（通用 Map 入参版本，保留向下兼容）
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
     * 校验图形验证码（便捷强类型方法）
     *
     * @param captchaId 验证码标识
     * @param captchaCode 用户输入的验证码文字
     */
    fun verifyCaptcha(captchaId: String, captchaCode: String): Flow<NetworkResponse<Boolean>> =
        verifyCaptcha(mapOf("id" to captchaId, "code" to captchaCode))

    /**
     * 校验图形验证码（通用 Map 入参版本，保留向下兼容）
     */
    fun verifyCaptcha(params: Map<String, String>): Flow<NetworkResponse<Boolean>> = flow {
        emit(authNetworkDataSource.verifyCaptcha(params))
    }.flowOn(ioDispatcher)

    // ==================================================================================
    // 令牌与本地会话管理
    // ==================================================================================

    /**
     * 刷新 Token（便捷强类型方法）
     */
    fun refreshToken(refreshToken: String): Flow<NetworkResponse<Auth>> =
        refreshToken(mapOf("refreshToken" to refreshToken))

    /**
     * 刷新 Token（通用 Map 入参版本，保留向下兼容）
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
