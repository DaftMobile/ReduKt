package dev.redukt.data.resolver

import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.data.DataSource
import dev.redukt.data.PureDataSourceKey

public interface DataSourceResolver : DispatchClosure.Element {

    public fun <T : DataSource<*, *>> resolve(key: PureDataSourceKey<T>): T

    override val key: Key get() = Key

    public companion object Key : DispatchClosure.Key<DataSourceResolver>
}

public val DispatchScope<*>.dataSourceResolver: DataSourceResolver get() = closure[DataSourceResolver]
