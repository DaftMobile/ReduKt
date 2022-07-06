package com.daftmobile.redukt.data.source

import com.daftmobile.redukt.core.scope.DispatchScope

interface DataSource<Request, Response> {
    suspend fun get(request: Request): Response
}

suspend fun <State, Request, Response> DispatchScope<State>.callDataSource(
    key: DataSourceKey<Request, Response>,
    request: Request
): Response = dispatchContext[DataSourcesConfig].resolver.resolve(key).get(request)