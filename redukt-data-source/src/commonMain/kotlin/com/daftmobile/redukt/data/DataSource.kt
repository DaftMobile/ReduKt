package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.scope.DispatchScope

public interface DataSource<Request, Response> {
    public suspend fun get(request: Request): Response
}

public interface DataSourceKey<Request, Response>

public suspend fun <State, Request, Response> DispatchScope<State>.callDataSource(
    key: DataSourceKey<Request, Response>,
    request: Request
): Response = dataSourceResolver.resolve(key).get(request)