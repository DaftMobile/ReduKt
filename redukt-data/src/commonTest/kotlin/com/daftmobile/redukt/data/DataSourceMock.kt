package com.daftmobile.redukt.data

@Suppress("UNCHECKED_CAST")
internal class DataSourceMock<Request, Response>(
    private val onCall: suspend (Request) -> Response = { null as Response }
) : DataSource<Request, Response> {
    override suspend fun call(request: Request): Response = onCall(request)

}