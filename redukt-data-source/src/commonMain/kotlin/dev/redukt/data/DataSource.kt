package dev.redukt.data

import dev.redukt.core.DispatchScope
import dev.redukt.data.resolver.DataSourceResolver
import dev.redukt.data.resolver.dataSourceResolver

/**
 * Any asynchronous source of data that provides [Response] on a [call] with a [Request].
 *
 */
public interface DataSource<in Request, out Response> {

    /**
     * Provides a [Response] for a [Request].
     */
    public suspend fun call(request: Request): Response
}

/**
 * Identifies a [DataSource] of type [T].
 */
public interface PureDataSourceKey<T : DataSource<*, *>>

public typealias DataSourceKey<Request, Response> = PureDataSourceKey<DataSource<Request, Response>>

/**
 * Calls a [DataSource] resolved with a [DataSourceResolver] from a [DispatchScope.closure].
 */
public suspend fun <Request, Response> DispatchScope<*>.callDataSource(
    key: PureDataSourceKey<DataSource<Request, Response>>,
    request: Request
): Response = dataSourceResolver.call(key, request)

/**
 * Resolves a [DataSource] identified by a given [key] and calls with a given [request].
 */
public suspend fun <Request, Response> DataSourceResolver.call(
    key: PureDataSourceKey<DataSource<Request, Response>>,
    request: Request
): Response = this.resolve(key).call(request)