package com.seanchen.xinchat.core.network.service

import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * 个人资料相关接口
 *
 * 路径以服务端实际注册的路由为准：个人资料与账号操作位于 user/info 前缀下，
 * 修改密码复用认证模块的邮箱验证码入口 auth/updatePassword。
 */
interface UserInfoService {
    /**
     * 修改个人资料
     */
    @POST("user/info/updatePersonInfo")
    suspend fun updatePersonInfo(@Body params: Map<String, Any>): NetworkResponse<Any>

    /**
     * 修改密码，请求体为 email、code、newPassword
     */
    @POST("auth/updatePassword")
    suspend fun updatePassword(@Body params: Map<String, String>): NetworkResponse<Any>

    /**
     * 注销当前账号
     */
    @POST("user/info/logoff")
    suspend fun logoff(@Body params: Map<String, Any>): NetworkResponse<Boolean>

    /**
     * 绑定手机号，服务端暂未开放
     */
    @POST("user/info/bindPhone")
    suspend fun bindPhone(@Body params: Map<String, String>): NetworkResponse<Any>

    /**
     * 查询当前登录用户的个人资料
     */
    @GET("user/info/person")
    suspend fun getPersonInfo(): NetworkResponse<User>
}
