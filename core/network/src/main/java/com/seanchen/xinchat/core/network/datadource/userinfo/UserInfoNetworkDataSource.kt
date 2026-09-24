package com.seanchen.xinchat.core.network.datadource.userinfo

import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.NetworkResponse
import okhttp3.MultipartBody

interface UserInfoNetworkDataSource {

    /**
     * 上传用户头像
     *
     * @param file 头像文件部件
     * @return 更新后的用户信息响应
     */
    suspend fun uploadAvatar(file: MultipartBody.Part): NetworkResponse<User>

    /**
     * 修改用户昵称
     *
     * @param request 包含新昵称的请求对象
     * @return 更新后的用户信息响应
     */
    suspend fun updateNickname(request: com.seanchen.xinchat.core.model.request.UpdateNicknameRequest): NetworkResponse<User>

    /**
     * 更新用户个人信息
     *
     * @param params 用户信息参数
     * @return 更新结果响应
     * @author Joker.X
     */
    suspend fun updatePersonInfo(params: Map<String, Any>): NetworkResponse<Any>

    /**
     * 更新用户密码
     *
     * @param params 密码参数
     * @return 更新结果响应
     * @author Joker.X
     */
    suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Any>

    /**
     * 注销账号
     *
     * @param params 注销参数
     * @return 注销结果响应
     * @author Joker.X
     */
    suspend fun logoff(params: Map<String, Any>): NetworkResponse<Boolean>

    /**
     * 绑定手机号
     *
     * @param params 绑定参数
     * @return 绑定结果响应
     * @author Joker.X
     */
    suspend fun bindPhone(params: Map<String, String>): NetworkResponse<Any>

    /**
     * 获取用户个人信息
     *
     * @return 用户信息响应
     * @author Joker.X
     */
    suspend fun getPersonInfo(): NetworkResponse<User>
}
