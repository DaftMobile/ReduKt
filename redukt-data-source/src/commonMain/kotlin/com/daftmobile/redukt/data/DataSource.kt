package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.DispatchScope

public interface DataSource<in Request, out Response> {
    public suspend fun call(request: Request): Response
}

public interface DataSourceKey<Request, Response>

public suspend fun <State, Request, Response> DispatchScope<State>.callDataSource(
    key: DataSourceKey<Request, Response>,
    request: Request
): Response = dataSourceResolver.call(key, request)

public suspend fun <Request, Response> DataSourceResolver.call(
    key: DataSourceKey<Request, Response>,
    request: Request
): Response = this.resolve(key).call(request)