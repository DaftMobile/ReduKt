package dev.redukt.data

import dev.redukt.core.Action

/**
 * Returns [this] as [T] if it is an instance of [DataSourceAction] associated with given [key].
 */
public inline fun <Request, Response, reified T : DataSourceAction<Request, Response>> Action?.asDataSourceAction(
    key: PureDataSourceKey<DataSource<Request, Response>>
): T? {
    return this?.takeIf { it is T && it.key == key } as? T
}

/**
 * Returns [this] as [T] if it is an instance of [DataSourceAction] associated with given [key].
 */
public inline fun <Request, Response, reified T : DataSourceAction<Request, Response>> DataSourceAction<*, *>?.asOf(
    key: PureDataSourceKey<DataSource<Request, Response>>
): T? {
    return this?.takeIf { it.key == key } as? T
}
