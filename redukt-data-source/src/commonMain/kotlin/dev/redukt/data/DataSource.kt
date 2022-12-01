package dev.redukt.data

import dev.redukt.core.DispatchScope

public interface DataSource<in Request, out Response> {
    public suspend fun call(request: Request): Response
}

public interface DataSourceKey<T : DataSource<*, *>>

public suspend fun <Request, Response> DispatchScope<*>.callDataSource(
    key: DataSourceKey<DataSource<Request, Response>>,
    request: Request
): Response = dataSourceResolver.call(key, request)

public suspend fun <Request, Response> DataSourceResolver.call(
    key: DataSourceKey<DataSource<Request, Response>>,
    request: Request
): Response = this.resolve(key).call(request)