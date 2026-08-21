package com.seanchen.xinchat.core.network.datadource.userinfo

import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.response.NetworkResponse
import com.seanchen.xinchat.core.network.base.BaseNetworkDataSource
import com.seanchen.xinchat.core.network.service.UserInfoService
import javax.inject.Inject

class UserInfoNetworkDataSourceImpl @Inject constructor(
    private val userInfoService: UserInfoService
) : BaseNetworkDataSource(), UserInfoNetworkDataSource {
    override suspend fun updatePersonInfo(params: Map<String, Any>): NetworkResponse<Any> {
        return userInfoService.updatePersonInfo(params)
    }

    override suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Any> {
        return userInfoService.updatePassword(params)
    }

    override suspend fun logoff(params: Map<String, Any>): NetworkResponse<Any> {
        return userInfoService.logoff(params)
    }

    override suspend fun bindPhone(params: Map<String, String>): NetworkResponse<Any> {
        return userInfoService.bindPhone(params)
    }

    override suspend fun getPersonInfo(): NetworkResponse<User> {
        return userInfoService.getPersonInfo()
    }
}