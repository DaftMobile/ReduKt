package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.closure.DispatchClosure

public interface DataSourceResolver : DispatchClosure.Element {

    public suspend fun <T : DataSource<*, *>> resolve(key: DataSourceKey<T>): T

    override val key: Key get() = Key

    public companion object Key : DispatchClosure.Key<DataSourceResolver>
}

public val DispatchScope<*>.dataSourceResolver: DataSourceResolver get() = closure[DataSourceResolver]
