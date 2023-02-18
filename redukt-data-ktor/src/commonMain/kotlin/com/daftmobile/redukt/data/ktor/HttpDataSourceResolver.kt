package com.daftmobile.redukt.data.ktor

import com.daftmobile.redukt.data.DataSource
import com.daftmobile.redukt.data.DataSourceKey
import com.daftmobile.redukt.data.PureDataSourceKey
import com.daftmobile.redukt.data.resolver.DataSourceResolver
import com.daftmobile.redukt.data.resolver.TypeSafeResolverConfigMarker
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public fun HttpDataSourceResolver(
    config: HttpResolverConfigScope.() -> Unit
): DataSourceResolver = HttpResolverBuilder().apply(config).build()

public interface HttpResolverConfigScope {

    public var dispatcher: CoroutineDispatcher
    public var defaultErrorMapper: HttpErrorMapper?

    @TypeSafeResolverConfigMarker
    public fun client(client: HttpClient)

    @TypeSafeResolverConfigMarker
    public fun client(block: HttpClientConfig<*>.() -> Unit)

    @TypeSafeResolverConfigMarker
    public fun client(engine: HttpClientEngine, block: HttpClientConfig<*>.() -> Unit)

    @TypeSafeResolverConfigMarker
    public infix fun <Request, Response, T : DataSource<Request, Response>> PureDataSourceKey<T>.resolvedBy(
        provider: () -> HttpEndpoint<Request, *, Response>
    )

    @TypeSafeResolverConfigMarker
    public infix fun <Request, Response, T : DataSource<Request, Response>> PureDataSourceKey<T>.resolvesTo(
        endpoint: HttpEndpoint<Request, *, Response>
    )
}

internal class HttpResolverBuilder : HttpResolverConfigScope {

    private var clientInstance: HttpClient? = null
    private val providers = mutableMapOf<DataSourceKey<*, *>, () -> HttpEndpoint<*, *, *>>()

    override var dispatcher: CoroutineDispatcher = Dispatchers.Default
    override var defaultErrorMapper: HttpErrorMapper? = null
    override fun client(client: HttpClient) {
        clientInstance = client
    }

    override fun client(block: HttpClientConfig<*>.() -> Unit) {
        clientInstance = HttpClient(block = block)
    }

    override fun client(engine: HttpClientEngine, block: HttpClientConfig<*>.() -> Unit) {
        clientInstance = HttpClient(engine, block)
    }

    override fun <Request, Response, T : DataSource<Request, Response>> PureDataSourceKey<T>.resolvedBy(
        provider: () -> HttpEndpoint<Request, *, Response>
    ) {
        providers[this] = { applyDefaultMapper(provider()) }
    }

    override fun <Request, Response, T : DataSource<Request, Response>> PureDataSourceKey<T>.resolvesTo(
        endpoint: HttpEndpoint<Request, *, Response>
    ) {
        val newEndpoint = applyDefaultMapper(endpoint)
        providers[this] = { newEndpoint }
    }

    private fun <Request, Response> applyDefaultMapper(
        endpoint: HttpEndpoint<Request, *, Response>
    ): HttpEndpoint<Request, *, Response> {
        val defaultMapper = defaultErrorMapper
        return if (endpoint.errorMapper == null && defaultMapper != null && endpoint is HttpEndpointDefinition) {
            endpoint.copy(errorMapper = defaultMapper)
        } else {
            endpoint
        }
    }

    fun build(): DataSourceResolver = DataSourceResolver {
        val client = clientInstance ?: HttpClient()
        providers.forEach { (key, endpoint) ->
            key resolvesTo HttpDataSource(client, dispatcher, endpoint())
        }
    }
}
