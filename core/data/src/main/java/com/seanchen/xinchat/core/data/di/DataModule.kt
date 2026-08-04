package com.seanchen.xinchat.core.data.di

import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.data.repository.ChatRepository
import com.seanchen.xinchat.core.data.repository.DefaultAuthRepository
import com.seanchen.xinchat.core.data.repository.DefaultChatRepository
import com.seanchen.xinchat.core.data.repository.DefaultFriendRepository
import com.seanchen.xinchat.core.data.repository.FriendRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataBindingsModule {
    @Binds
    @Singleton
    internal abstract fun bindAuthRepository(repository: DefaultAuthRepository): AuthRepository

    @Binds
    @Singleton
    internal abstract fun bindFriendRepository(repository: DefaultFriendRepository): FriendRepository

    @Binds
    @Singleton
    internal abstract fun bindChatRepository(repository: DefaultChatRepository): ChatRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineModule {
    @Provides
    @Singleton
    @ApplicationScope
    internal fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
