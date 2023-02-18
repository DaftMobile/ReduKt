package com.daftmobile.redukt.data.ktor

import com.daftmobile.redukt.data.DataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class HttpDataSource<in Request, RawResponse, out Response>(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher,
    private val endpoint: HttpEndpoint<Request, RawResponse, Response>
) : DataSource<Request, Response> {
    override suspend fun call(request: Request): Response = withContext(dispatcher) {
        try {
            val rawRequest = HttpRequestBuilder().apply { endpoint.requestCreator(this, request) }
            val httpResponse = client.request(rawRequest)
            val response = endpoint.responseReader(httpResponse)
            endpoint.responseMapper.invoke(request, response)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            throw (endpoint.errorMapper?.invoke(e) ?: e)
        }
    }
}
