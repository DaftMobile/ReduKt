package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.JobAction

public data class DataSourceCall<Request, Response>(
    val key: DataSourceKey<DataSource<Request, Response>>,
    val request: Request
) : JobAction

public data class DataSourceAction<Request, Response>(
    val key: DataSourceKey<DataSource<Request, Response>>,
    val payload: DataSourcePayload<Request, Response>,
) : Action

public sealed interface DataSourcePayload<Request, Response> {

    public val request: Request

    public data class Started<Request, Response>(override val request: Request) : DataSourcePayload<Request, Response>

    public data class Success<Request, Response>(
        override val request: Request,
        val response: Response,
    ) : DataSourcePayload<Request, Response>

    public data class Failure<Request, Response>(
        override val request: Request,
        val error: Throwable
    ) : DataSourcePayload<Request, Response>
}

public data class DataSourceResultPayload<Request, Response>(
    public val request: Request,
    public val result: Result<Response>
)