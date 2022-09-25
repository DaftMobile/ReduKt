package com.daftmobile.redukt.data

public open class TypeSafeDataSourceResolver(resolveConfig: TypeSafeResolveScope.() -> Unit) : DataSourceResolver {

    private val typeSafeResolve = TypeSafeResolveScopeImpl().apply(resolveConfig)

    @Suppress("UNCHECKED_CAST")
    override suspend fun  <T : DataSource<*, *>> resolve(key: DataSourceKey<T>): T {
        return typeSafeResolve.providers.getValue(key).invoke() as T
    }
}

@DslMarker
public annotation class TypeSafeDataSourceResolverMarker

public interface TypeSafeResolveScope {
    @TypeSafeDataSourceResolverMarker
    public infix fun  <T : DataSource<*, *>> DataSourceKey<T>.resolveBy(provider: () -> T)
}

internal class TypeSafeResolveScopeImpl(
    val providers: MutableMap<DataSourceKey<*>, () -> DataSource<*, *>> = mutableMapOf()
) : TypeSafeResolveScope {
    override infix fun  <T : DataSource<*, *>> DataSourceKey<T>.resolveBy(provider: () -> T) {
        providers[this] = provider
    }
}