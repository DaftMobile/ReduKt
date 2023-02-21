package com.daftmobile.redukt.data

internal class ChainedDataSourceResolver(
    private val resolvers: List<DataSourceResolver>
) : DataSourceResolver {

    override fun <T : DataSource<*, *>> resolve(key: PureDataSourceKey<T>): T? {
        return resolvers.firstNotNullOfOrNull { it.resolve(key) }
    }
}
