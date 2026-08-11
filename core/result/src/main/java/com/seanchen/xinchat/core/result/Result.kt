package com.seanchen.xinchat.core.result

import com.seanchen.xinchat.core.model.response.NetworkResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Result <out T>{
    data object Loading : Result<Nothing>

    data class Success<T>(val data: T) : Result<T>

    data class Error(val exception: Throwable) : Result<Nothing>
}

fun <T> Flow<NetworkResponse<T>>.asResult(): Flow<Result<NetworkResponse<T>>> =
    map<NetworkResponse<T>, Result<NetworkResponse<T>>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { throwable ->
            if (throwable is CancellationException) throw throwable
            emit(Result.Error(throwable))
        }
