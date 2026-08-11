package com.seanchen.xinchat.core.result

import com.seanchen.xinchat.core.model.response.NetworkResponse
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ResultFlowTest {

    @Test
    fun `asResult emits loading then success`() = runBlocking {
        val response = NetworkResponse(data = "ok")

        assertEquals(
            listOf(Result.Loading, Result.Success(response)),
            flowOf(response).asResult().toList()
        )
    }

    @Test
    fun `asResult emits error when upstream fails`() = runBlocking {
        val exception = IllegalStateException("network failed")

        val results = flow<NetworkResponse<String>> {
            throw exception
        }.asResult().toList()

        assertEquals(Result.Loading, results.first())
        assertEquals(exception, (results.last() as Result.Error).exception)
    }
}
