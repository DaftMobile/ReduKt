package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.DispatchScope

public interface DataSourceResolver : DispatchContext.Element {

    public suspend fun <Request, Response> resolve(key: DataSourceKey<Request, Response>): DataSource<Request, Response>

    override val key: Key get() = Key

    public companion object Key : DispatchContext.Key<DataSourceResolver>
}

public val <State> DispatchScope<State>.dataSourceResolver: DataSourceResolver get() = dispatchContext[DataSourceResolver]
