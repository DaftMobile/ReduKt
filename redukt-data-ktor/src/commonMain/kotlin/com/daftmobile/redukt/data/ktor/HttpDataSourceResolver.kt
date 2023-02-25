package com.daftmobile.redukt.data.ktor

import com.daftmobile.redukt.data.DataSource
import com.daftmobile.redukt.data.DataSourceKey
import com.daftmobile.redukt.data.PureDataSourceKey
import com.daftmobile.redukt.data.DataSourceResolver
import com.daftmobile.redukt.data.TypeSafeResolverConfigMarker
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Creates a [DataSourceResolver] integrated with [HttpClient]. It is based on association between [DataSourceKey]s
 * and [HttpEndpoint]s described with [config] block.
 *
 * On [DataSourceResolver.resolve] call it returns a [DataSource], only if there is [HttpEndpoint] associated
 * with given key. This [DataSource] performs an HTTP call and applies functions from a [HttpEndpoint] object.
 *
 * @see [HttpEndpoint]
 */
public fun HttpDataSourceResolver(
    config: HttpDataSourceResolverConfigScope.() -> Unit
): DataSourceResolver = HttpResolverBuilder().apply(config).build()

public interface HttpDataSourceResolverConfigScope {

    /**
     * Dispatcher that is used only to transformations associated with HTTP endpoint. Actual HTTP call
     * is performed with dispatcher associated with [HttpClient].
     */
    public var dispatcher: CoroutineDispatcher

    /**
     * [HttpErrorMapper] that is injected into [HttpEndpoint]s that returns null from [HttpEndpoint.errorMapper].
     */
    public var defaultErrorMapper: HttpErrorMapper?

    /**
     * Sets a [client] for this [DataSourceResolver].
     * It overwrites previous [HttpDataSourceResolverConfigScope.client] calls.
     */
    @TypeSafeResolverConfigMarker
    public fun client(client: HttpClient)

    /**
     * Sets an instance of [HttpClient] with given [config] for this [DataSourceResolver].
     * It overwrites previous [HttpDataSourceResolverConfigScope.client] calls.
     */
    @TypeSafeResolverConfigMarker
    public fun client(config: HttpClientConfig<*>.() -> Unit)

    /**
     * Sets an instance of [HttpClient] with given [config] and [engine] for this [DataSourceResolver].
     * It overwrites previous [HttpDataSourceResolverConfigScope.client] calls.
     */
    @TypeSafeResolverConfigMarker
    public fun client(engine: HttpClientEngine, config: HttpClientConfig<*>.() -> Unit)

    /**
     * Associates [this] key with [HttpEndpoint] provided by [provider].
     */
    @TypeSafeResolverConfigMarker
    public infix fun <Request, Response, T : DataSource<Request, Response>> PureDataSourceKey<T>.resolvedBy(
        provider: () -> HttpEndpoint<Request, *, Response>
    )

    /**
     * Associates [this] key with [endpoint].
     */
    @TypeSafeResolverConfigMarker
    public infix fun <Request, Response, T : DataSource<Request, Response>> PureDataSourceKey<T>.resolvesTo(
        endpoint: HttpEndpoint<Request, *, Response>
    )
}

internal class HttpResolverBuilder : HttpDataSourceResolverConfigScope {

    private var clientInstance: HttpClient? = null
    private val providers = mutableMapOf<DataSourceKey<*, *>, () -> HttpEndpoint<*, *, *>>()

    override var dispatcher: CoroutineDispatcher = Dispatchers.Default
    override var defaultErrorMapper: HttpErrorMapper? = null
    override fun client(client: HttpClient) {
        clientInstance = client
    }

    override fun client(config: HttpClientConfig<*>.() -> Unit) {
        clientInstance = HttpClient(block = config)
    }

    override fun client(engine: HttpClientEngine, config: HttpClientConfig<*>.() -> Unit) {
        clientInstance = HttpClient(engine, config)
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
