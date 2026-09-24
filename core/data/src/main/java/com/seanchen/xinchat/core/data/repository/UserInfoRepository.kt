package com.seanchen.xinchat.core.data.repository

import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.datadource.userinfo.UserInfoNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject


/**
 * 用户信息相关仓库
 *
 * @param userInfoNetworkDataSource 用户信息网络数据源
 * @author Joker.X
 */
class UserInfoRepository @Inject constructor(
    private val userInfoNetworkDataSource: UserInfoNetworkDataSource
) {
    /**
     * 上传用户头像
     *
     * @param file 头像文件部件
     * @return 更新后的用户信息Flow
     */
    fun uploadAvatar(file: MultipartBody.Part): Flow<NetworkResponse<User>> = flow {
        emit(userInfoNetworkDataSource.uploadAvatar(file))
    }.flowOn(Dispatchers.IO)

    /**
     * 修改用户昵称
     *
     * @param request 包含新昵称的请求对象
     * @return 更新后的用户信息Flow
     */
    fun updateNickname(request: com.seanchen.xinchat.core.model.request.UpdateNicknameRequest): Flow<NetworkResponse<User>> = flow {
        emit(userInfoNetworkDataSource.updateNickname(request))
    }.flowOn(Dispatchers.IO)

    /**
     * 更新用户信息
     *
     * @param params 更新参数
     * @return 更新结果Flow
     * @author Joker.X
     */
    fun updatePersonInfo(params: Map<String, Any>): Flow<NetworkResponse<Any>> = flow {
        emit(userInfoNetworkDataSource.updatePersonInfo(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 更新用户密码
     *
     * @param params 密码更新参数
     * @return 更新结果Flow
     * @author Joker.X
     */
    fun updatePassword(params: Map<String, String>): Flow<NetworkResponse<Any>> = flow {
        emit(userInfoNetworkDataSource.updatePassword(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 注销账户
     *
     * @param params 注销参数
     * @return 注销结果Flow
     * @author Joker.X
     */
    fun logoff(params: Map<String, Any>): Flow<NetworkResponse<Boolean>> = flow {
        emit(userInfoNetworkDataSource.logoff(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 绑定手机号
     *
     * @param params 手机号绑定参数
     * @return 绑定结果Flow
     * @author Joker.X
     */
    fun bindPhone(params: Map<String, String>): Flow<NetworkResponse<Any>> = flow {
        emit(userInfoNetworkDataSource.bindPhone(params))
    }.flowOn(Dispatchers.IO)

    /**
     * 获取用户个人信息
     *
     * @return 用户信息Flow
     * @author Joker.X
     */
    fun getPersonInfo(): Flow<NetworkResponse<User>> = flow {
        emit(userInfoNetworkDataSource.getPersonInfo())
    }.flowOn(Dispatchers.IO)
}
