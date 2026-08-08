package com.seanchen.xinchat.core.data.state

import com.seanchen.xinchat.core.data.di.ApplicationScope
import com.seanchen.xinchat.core.model.entity.Auth
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


    /**
     * 初始化应用状态
     */
    fun initialize(){
        applicationScope.launch {
            initializeState()
        }
    }

    /**
     * 从本地存储初始化应用状态
     */
    private suspend fun initializeState(){
        // 获取认证信息

    }
}