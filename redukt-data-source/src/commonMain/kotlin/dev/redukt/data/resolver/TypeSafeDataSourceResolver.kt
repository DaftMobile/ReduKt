package dev.redukt.data.resolver

import dev.redukt.data.DataSource
import dev.redukt.data.PureDataSourceKey

public open class TypeSafeDataSourceResolver(resolveConfig: TypeSafeResolveScope.() -> Unit) : DataSourceResolver {

    private val typeSafeResolve = TypeSafeResolveScopeImpl().apply(resolveConfig)

    @Suppress("UNCHECKED_CAST")
    override fun  <T : DataSource<*, *>> resolve(key: PureDataSourceKey<T>): T {
        return typeSafeResolve.providers[key]?.invoke() as? T ?: throw MissingDataSourceException(key)
    }
}

@DslMarker
public annotation class TypeSafeDataSourceResolverMarker

public interface TypeSafeResolveScope {
    @TypeSafeDataSourceResolverMarker
    public infix fun  <T : DataSource<*, *>> PureDataSourceKey<T>.resolveBy(provider: () -> T)
}

internal class TypeSafeResolveScopeImpl(
    val providers: MutableMap<PureDataSourceKey<*>, () -> DataSource<*, *>> = mutableMapOf()
) : TypeSafeResolveScope {
    override infix fun  <T : DataSource<*, *>> PureDataSourceKey<T>.resolveBy(provider: () -> T) {
        providers[this] = provider
    }
}

internal class MissingDataSourceException(key: PureDataSourceKey<*>) : Exception("DataSource with $key is not defined in the DataSourceResolver!")