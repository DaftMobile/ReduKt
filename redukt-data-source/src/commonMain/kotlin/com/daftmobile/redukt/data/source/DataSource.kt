package com.daftmobile.redukt.data.source

import com.daftmobile.redukt.core.scope.DispatchScope

public interface DataSource<Request, Response> {
    public suspend fun get(request: Request): Response
}

public interface DataSourceKey<Request, Response>

public suspend fun <State, Request, Response> DispatchScope<State>.callDataSource(
    key: DataSourceKey<Request, Response>,
    request: Request
): Response = dispatchContext[DataSourcesConfig].resolver.resolve(key).get(request)