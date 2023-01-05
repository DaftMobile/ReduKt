package dev.redukt.data

import dev.redukt.core.DispatchScope
import dev.redukt.data.resolver.DataSourceResolver
import dev.redukt.data.resolver.dataSourceResolver

public interface DataSource<in Request, out Response> {
    public suspend fun call(request: Request): Response
}

public interface PureDataSourceKey<T : DataSource<*, *>>

public typealias DataSourceKey<Request, Response> = PureDataSourceKey<DataSource<Request, Response>>

public suspend fun <Request, Response> DispatchScope<*>.callDataSource(
    key: PureDataSourceKey<DataSource<Request, Response>>,
    request: Request
): Response = dataSourceResolver.call(key, request)

public suspend fun <Request, Response> DataSourceResolver.call(
    key: PureDataSourceKey<DataSource<Request, Response>>,
    request: Request
): Response = this.resolve(key).call(request)