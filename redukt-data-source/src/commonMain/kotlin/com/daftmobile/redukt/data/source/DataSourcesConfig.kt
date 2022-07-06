package com.daftmobile.redukt.data.source

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.scope.DispatchScope

data class DataSourcesConfig(val resolver: DataSourceResolver) : DispatchContext.Element {
    override val key = Key

    companion object Key : DispatchContext.Key<DataSourcesConfig>
}

val <State> DispatchScope<State>.dataSourceResolver get() = dispatchContext[DataSourcesConfig].resolver