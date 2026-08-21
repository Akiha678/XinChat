package com.seanchen.xinchat.core.datastore.di

import com.seanchen.xinchat.core.datastore.datasource.auth.AuthStoreDataSource
import com.seanchen.xinchat.core.datastore.datasource.auth.AuthStoreDataSourceImpl
import com.seanchen.xinchat.core.datastore.datasource.userinfo.UserInfoStoreDataSource
import com.seanchen.xinchat.core.datastore.datasource.userinfo.UserInfoStoreDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {
    @Binds
    @Singleton
    abstract fun bindAuthStoreDataSource(
        impl: AuthStoreDataSourceImpl
    ): AuthStoreDataSource

    @Binds
    @Singleton
    abstract fun bindUserInfoStoreDataSource(
        impl: UserInfoStoreDataSourceImpl
    ): UserInfoStoreDataSource
}