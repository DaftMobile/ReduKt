package com.daftmobile.redukt.data.source

import com.daftmobile.redukt.core.Action

interface DataSourceKey<Request, Response>

data class DataSourceCall<Request, Response>(
    val key: DataSourceKey<Request, Response>,
    val request: Request
) : Action

data class DataSourceAction<Request, Response>(
    val key: DataSourceKey<Request, Response>,
    val payload: DataSourcePayload<Request, Response>,
) : Action

sealed interface DataSourcePayload<Request, Response> {
    val request: Request

    data class Started<Request, Response>(override val request: Request) : DataSourcePayload<Request, Response>
    data class Success<Request, Response>(
        override val request: Request,
        val response: Response,
    ) : DataSourcePayload<Request, Response>

    data class Failure<Request, Response>(
        override val request: Request,
        val error: Throwable
    ) : DataSourcePayload<Request, Response>
}