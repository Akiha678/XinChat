package com.seanchen.xinchat.core.network.di

import com.seanchen.xinchat.core.network.datadource.auth.AuthNetworkDataSource
import com.seanchen.xinchat.core.network.datadource.auth.AuthNetworkDataSourceImpl
import com.seanchen.xinchat.core.network.datadource.userinfo.UserInfoNetworkDataSource
import com.seanchen.xinchat.core.network.datadource.userinfo.UserInfoNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAuthNetworkDataSource(
        impl: AuthNetworkDataSourceImpl
    ): AuthNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindUserInfoNetworkDataSource(
        impl: UserInfoNetworkDataSourceImpl
    ): UserInfoNetworkDataSource
}
