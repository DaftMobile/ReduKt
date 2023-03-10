package com.daftmobile.redukt.data

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.closure.DispatchClosure

/**
 * Provides [DataSource]s associated with [PureDataSourceKey]s.
 */
public interface DataSourceResolver : DispatchClosure.Element {

    /**
     * Returns a [DataSource] for a given [key]
     */
    public fun <T : DataSource<*, *>> resolve(key: PureDataSourceKey<T>): T?

    override val key: Key get() = Key

    public companion object Key : DispatchClosure.Key<DataSourceResolver> {

        /**
         * Creates a [DataSourceResolver] that delegates key resolve to [resolvers]. It calls [DataSourceResolver.resolve]
         * on each resolver until first non-null [DataSource].
         */
        public fun chain(resolvers: List<DataSourceResolver>): DataSourceResolver = ChainedDataSourceResolver(resolvers)

        /**
         * Creates a [DataSourceResolver] that delegates key resolve to [resolvers]. It calls [DataSourceResolver.resolve]
         * on each resolver until first non-null [DataSource].
         */
        public fun chain(vararg resolvers: DataSourceResolver): DataSourceResolver = chain(resolvers.toList())
    }
}

/**
 * Returns [DataSourceResolver] instance associated with a store. It depends on [DataSourceResolver] element injected
 * into the closure.
 */
public val DispatchScope<*>.dataSourceResolver: DataSourceResolver get() = closure[DataSourceResolver]

/**
 * Creates a [DataSourceResolver] with a [config] with type-safe DSL.
 */
public fun DataSourceResolver(
    config: TypeSafeResolverConfigScope.() -> Unit
): DataSourceResolver = TypeSafeDataSourceResolver(config)

public class MissingDataSourceException(
    key: PureDataSourceKey<*>
) : Exception("DataSource with $key is not defined in the DataSourceResolver!")

@DslMarker
public annotation class TypeSafeResolverConfigMarker

/**
 * The scope for configuring [DataSourceResolver] with a type-safe DSL.
 */
public interface TypeSafeResolverConfigScope {
    @TypeSafeResolverConfigMarker
    public infix fun <T : DataSource<*, *>> PureDataSourceKey<T>.resolveBy(provider: () -> T)

    @TypeSafeResolverConfigMarker
    public infix fun <T : DataSource<*, *>> PureDataSourceKey<T>.resolvesTo(dataSource: T)
}

private class TypeSafeDataSourceResolver(resolveConfig: TypeSafeResolverConfigScope.() -> Unit) : DataSourceResolver {

    private val typeSafeResolve = TypeSafeResolverConfigScopeImpl().apply(resolveConfig)

    @Suppress("UNCHECKED_CAST")
    override fun <T : DataSource<*, *>> resolve(key: PureDataSourceKey<T>): T? {
        return typeSafeResolve.providers[key]?.invoke() as? T
    }
}

private class TypeSafeResolverConfigScopeImpl(
    val providers: MutableMap<PureDataSourceKey<*>, () -> DataSource<*, *>> = mutableMapOf()
) : TypeSafeResolverConfigScope {
    override infix fun <T : DataSource<*, *>> PureDataSourceKey<T>.resolveBy(provider: () -> T) {
        providers[this] = provider
    }

    override fun <T : DataSource<*, *>> PureDataSourceKey<T>.resolvesTo(dataSource: T) {
        providers[this] = { dataSource }
    }
}
