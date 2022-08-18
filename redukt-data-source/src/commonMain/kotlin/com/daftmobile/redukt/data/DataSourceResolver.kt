package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.ClosureScope
import com.daftmobile.redukt.core.closure.DispatchClosure

public interface DataSourceResolver : DispatchClosure.Element {

    public suspend fun <Request, Response> resolve(key: DataSourceKey<Request, Response>): DataSource<Request, Response>

    override val key: Key get() = Key

    public companion object Key : DispatchClosure.Key<DataSourceResolver>
}

public val ClosureScope.dataSourceResolver: DataSourceResolver get() = closure[DataSourceResolver]
