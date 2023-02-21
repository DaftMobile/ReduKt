package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.DispatchScope

/**
 * Any asynchronous source of data that provides [Response] on a [call] with a [Request].
 */
public interface DataSource<in Request, out Response> {

    /**
     * Provides a [Response] for a [Request].
     */
    public suspend fun call(request: Request): Response
}

/**
 * Creates a [DataSource] with given [block] invoked on each [DataSource.call].
 */
public inline fun <Request, Response> DataSource(
    crossinline block: suspend (Request) -> Response
): DataSource<Request, Response> = object : DataSource<Request, Response> {
    override suspend fun call(request: Request): Response = block(request)
}

/**
 * Identifies a [DataSource] of type [T].
 */
public interface PureDataSourceKey<out T : DataSource<*, *>>

public typealias DataSourceKey<Request, Response> = PureDataSourceKey<DataSource<Request, Response>>

/**
 * Calls a [DataSource] resolved with a [DataSourceResolver] from a [DispatchScope.closure].
 *
 * @throws MissingDataSourceException when [DataSource] with given [key] could not be resolved.
 */
public suspend fun <Request, Response> DispatchScope<*>.callDataSource(
    key: PureDataSourceKey<DataSource<Request, Response>>,
    request: Request
): Response = dataSourceResolver.call(key, request)

/**
 * Resolves a [DataSource] identified by a given [key] and calls with a given [request].
 *
 * @throws MissingDataSourceException when [DataSource] with given [key] could not be resolved.
 */
public suspend fun <Request, Response> DataSourceResolver.call(
    key: PureDataSourceKey<DataSource<Request, Response>>,
    request: Request
): Response = this.resolve(key)?.call(request) ?: throw MissingDataSourceException(key)
