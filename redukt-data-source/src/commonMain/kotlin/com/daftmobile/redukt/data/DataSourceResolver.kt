package com.daftmobile.redukt.data

public interface DataSourceResolver {
    public suspend fun <Request, Response> resolve(key: DataSourceKey<Request, Response>): DataSource<Request, Response>
}

