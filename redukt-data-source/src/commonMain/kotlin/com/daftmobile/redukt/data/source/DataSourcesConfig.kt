package com.daftmobile.redukt.data.source

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.scope.DispatchScope

public data class DataSourcesConfig(val resolver: DataSourceResolver) : DispatchContext.Element {
    override val key: Key = Key

    public companion object Key : DispatchContext.Key<DataSourcesConfig>
}

public val <State> DispatchScope<State>.dataSourceResolver: DataSourceResolver get() = dispatchContext[DataSourcesConfig].resolver