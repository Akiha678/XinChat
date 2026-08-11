package com.seanchen.xinchat.core.network.base

import retrofit2.Retrofit

abstract class BaseNetworkDataSource {
    protected inline fun <reified T> Retrofit.createService(): T {
        return this.create(T::class.java)
    }
}