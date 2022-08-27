package com.daftmobile.redukt.data

public fun <Request, OriginRequest, Response> DataSource<OriginRequest, Response>.adaptRequest(
    transform: suspend (Request) -> OriginRequest
): DataSource<Request, Response> = object : DataSource<Request, Response> {
    override suspend fun call(request: Request): Response = this@adaptRequest.call(transform(request))
}

public fun <Request, OriginResponse, Response> DataSource<Request, OriginResponse>.adaptResponse(
    transform: suspend (OriginResponse) -> Response
): DataSource<Request, Response> = object : DataSource<Request, Response> {
    override suspend fun call(request: Request): Response = transform(this@adaptResponse.call(request))
}