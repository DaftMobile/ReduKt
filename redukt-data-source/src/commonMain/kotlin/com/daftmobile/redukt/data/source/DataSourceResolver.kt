package com.daftmobile.redukt.data.source

public interface DataSourceResolver {
    public suspend fun <Request, Response> resolve(key: DataSourceKey<Request, Response>): DataSource<Request, Response>
}

