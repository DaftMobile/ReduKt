package dev.redukt.data.resolver

import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.data.DataSource
import dev.redukt.data.PureDataSourceKey

/**
 * Provides [DataSource]s associated with [PureDataSourceKey]s.
 */
public interface DataSourceResolver : DispatchClosure.Element {

    /**
     * Returns a [DataSource] for a given [key]
     */
    public fun <T : DataSource<*, *>> resolve(key: PureDataSourceKey<T>): T?

    override val key: Key get() = Key

    public companion object Key : DispatchClosure.Key<DataSourceResolver>
}

/**
 * Returns [DataSourceResolver] instance associated with a store. It depends on [DataSourceResolver] element injected into the closure.
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
}