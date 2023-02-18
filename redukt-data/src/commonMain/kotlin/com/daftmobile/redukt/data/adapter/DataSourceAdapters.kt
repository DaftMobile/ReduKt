package com.daftmobile.redukt.data.adapter

import com.daftmobile.redukt.data.DataSource

/**
 * Adapts a [DataSource] with [transform] to accept a [Request] instead of a [OriginRequest].
 */
public fun <Request, OriginRequest, Response> DataSource<OriginRequest, Response>.adaptRequest(
    transform: suspend (Request) -> OriginRequest
): DataSource<Request, Response> = object : DataSource<Request, Response> {
    override suspend fun call(request: Request): Response = this@adaptRequest.call(transform(request))
}

/**
 * Adapts a [DataSource] with [transform] to return a [Response] instead of a [OriginResponse].
 * */
public fun <Request, OriginResponse, Response> DataSource<Request, OriginResponse>.adaptResponse(
    transform: suspend (OriginResponse) -> Response
): DataSource<Request, Response> = object : DataSource<Request, Response> {
    override suspend fun call(request: Request): Response = transform(this@adaptResponse.call(request))
}
