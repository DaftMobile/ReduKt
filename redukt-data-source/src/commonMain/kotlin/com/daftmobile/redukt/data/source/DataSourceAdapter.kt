package com.daftmobile.redukt.data.source

fun <Request, OriginRequest, Response> DataSource<OriginRequest, Response>.adaptRequest(
    transform: suspend (Request) -> OriginRequest
): DataSource<Request, Response> = object : DataSource<Request, Response> {
    override suspend fun get(request: Request): Response = this@adaptRequest.get(transform(request))
}

fun <Request, OriginResponse, Response> DataSource<Request, OriginResponse>.adaptResponse(
    transform: suspend (OriginResponse) -> Response
): DataSource<Request, Response> = object : DataSource<Request, Response> {
    override suspend fun get(request: Request): Response = transform(this@adaptResponse.get(request))
}