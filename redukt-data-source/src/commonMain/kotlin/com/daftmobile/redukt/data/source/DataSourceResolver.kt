package com.daftmobile.redukt.data.source

interface DataSourceResolver {
    suspend fun <Request, Response> resolve(key: DataSourceKey<Request, Response>): DataSource<Request, Response>
}

