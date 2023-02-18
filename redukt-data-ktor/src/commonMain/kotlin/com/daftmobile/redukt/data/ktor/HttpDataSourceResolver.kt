package com.daftmobile.redukt.data.ktor

import com.daftmobile.redukt.data.DataSource
import com.daftmobile.redukt.data.DataSourceKey
import com.daftmobile.redukt.data.PureDataSourceKey
import com.daftmobile.redukt.data.resolver.DataSourceResolver
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public fun HttpDataSourceResolver(
    client: HttpClient = HttpClient(),
    config: HttpResolverConfigScope.() -> Unit
): DataSourceResolver = HttpResolverConfigScopeImpl(client)
    .apply(config)
    .build()

public interface HttpResolverConfigScope {

    public var dispatcher: CoroutineDispatcher
    public var defaultErrorMapper: HttpErrorMapper?

    public infix fun <Request, Response, T : DataSource<Request, Response>> PureDataSourceKey<T>.resolvedBy(
        provider: () -> HttpEndpoint<Request, *, Response>
    )

    public infix fun <Request, Response, T : DataSource<Request, Response>> PureDataSourceKey<T>.resolvesTo(
        endpoint: HttpEndpoint<Request, *, Response>
    )
}

private class HttpResolverConfigScopeImpl(
    private val client: HttpClient,
) : HttpResolverConfigScope {

    private val providers = mutableMapOf<DataSourceKey<*, *>, () -> HttpEndpoint<*, *, *>>()
    override var dispatcher: CoroutineDispatcher = Dispatchers.Default
    override var defaultErrorMapper: HttpErrorMapper? = null

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
        providers.forEach { (key, endpoint) ->
            key resolvesTo HttpDataSource(client, dispatcher, endpoint())
        }
    }
}
