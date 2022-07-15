package com.daftmobile.redukt.data.source

public open class TypeSafeDataSourceResolver(resolveConfig: TypeSafeResolveScope.() -> Unit) : DataSourceResolver {

    private val typeSafeResolve = TypeSafeResolveScopeImpl().apply(resolveConfig)

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Request, Response> resolve(key: DataSourceKey<Request, Response>): DataSource<Request, Response> {
        return typeSafeResolve.providers.getValue(key).invoke() as DataSource<Request, Response>
    }
}

@DslMarker
public annotation class TypeSafeDataSourceResolverMarker

public interface TypeSafeResolveScope {
    @TypeSafeDataSourceResolverMarker
    public infix fun <Request, Response> DataSourceKey<Request, Response>.resolveBy(provider: () -> DataSource<Request, Response>)
}

internal class TypeSafeResolveScopeImpl(
    val providers: MutableMap<DataSourceKey<*, *>, () -> DataSource<*, *>> = mutableMapOf()
) : TypeSafeResolveScope {
    override infix fun <Request, Response> DataSourceKey<Request, Response>.resolveBy(provider: () -> DataSource<Request, Response>) {
        providers[this] = provider
    }
}