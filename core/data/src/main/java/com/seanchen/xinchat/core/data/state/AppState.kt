package com.seanchen.xinchat.core.data.state

import com.seanchen.xinchat.core.data.di.ApplicationScope
import com.seanchen.xinchat.core.data.repository.AuthStoreRepository
import com.seanchen.xinchat.core.data.repository.UserInfoRepository
import com.seanchen.xinchat.core.data.repository.UserInfoStoreRepository
import com.seanchen.xinchat.core.model.entity.Auth
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.result.ResultHandler
import com.seanchen.xinchat.core.result.SessionExpiryNotifier
import com.seanchen.xinchat.core.result.asResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppState @Inject constructor(
    private val authStoreRepository: AuthStoreRepository,
    private val userInfoStoreRepository: UserInfoStoreRepository,
    private val userInfoRepository: UserInfoRepository,
    @param:ApplicationScope private val applicationScope: CoroutineScope
){

    // 用户登录状态
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // 用户ID
    private val _userId = MutableStateFlow(0L)
    val userId: StateFlow<Long> = _userId.asStateFlow()

    // 用户授权信息
    private val _auth = MutableStateFlow<Auth?>(null)
    val auth: StateFlow<Auth?> = _auth.asStateFlow()

    // 用户信息
    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()

    init {
        observeSessionExpiry()
    }

    /**
     * 监听登录态失效，清理本地会话
     *
     * 只有当前确实处于登录态时才清理：登录、注册接口用 401 表示凭证错误，
     * 那种情况下本地本来就没有会话，不应触发登出。
     */
    private fun observeSessionExpiry() {
        applicationScope.launch {
            SessionExpiryNotifier.events.collect {
                if (_isLoggedIn.value) {
                    logout()
                }
            }
        }
    }


    /**
     * 初始化应用状态
     */
    suspend fun initialize() {
        initializeState()
    }

    /**
     * 从本地存储初始化应用状态
     */
    private suspend fun initializeState(){
        // 获取认证信息
        val authData = authStoreRepository.getAuth()
        val loggedIn = authStoreRepository.isLoggedIn()
        _isLoggedIn.value = loggedIn
        _auth.value = authData

        if (loggedIn) {
            val user = userInfoStoreRepository.getUserInfo()
            _userInfo.value = user
            _userId.value = user?.id ?: 0L
        }
    }

    /**
     * 更新用户登录状态
     */
    suspend fun updateUserState(auth: Auth, user: User) {
        authStoreRepository.saveAuth(auth)
        userInfoStoreRepository.saveUserInfo(user)

        _auth.value = auth
        _userInfo.value = user
        _userId.value = user.id
        _isLoggedIn.value = true
    }

    /**
     * 更新用户信息
     */
    suspend fun updateUserInfo(user: User) {
        userInfoStoreRepository.saveUserInfo(user)

        // 更新内存中的状态
        _userInfo.value = user
        _userId.value = user.id
    }

    /**
     * 更新认证信息
     */
    suspend fun updateAuth(auth: Auth) {
        authStoreRepository.saveAuth(auth)

        _auth.value = auth

        _isLoggedIn.value = true
    }

    /**
     * 用户登出
     */
    suspend fun logout(){
        authStoreRepository.clearAuth()
        userInfoStoreRepository.clearUserInfo()

        // 重置内存中的状态
        _isLoggedIn.value = false
        _auth.value = null
        _userInfo.value = null
        _userId.value = 0L
    }

    suspend fun shouldRefreshToken(): Boolean {
        return authStoreRepository.shouldRefreshToken()
    }


    fun refreshUserInfo() {
        if (!_isLoggedIn.value) return
        ResultHandler.handleResultWithData(
            scope = applicationScope,
            flow = userInfoRepository.getPersonInfo().asResult(),
            onData = { data ->
                applicationScope.launch {
                    updateUserInfo(data)
                }
            }
        )
    }
}
