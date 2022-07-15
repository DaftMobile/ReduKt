package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.middleware.MiddlewareStatus
import kotlin.test.assertEquals

public fun MiddlewareStatus.expectToBeConsumed(): Unit = assertEquals(
    MiddlewareStatus.Consumed,
    this,
    "Action expected to be consumed!"
)

public fun MiddlewareStatus.expectToBeNext(action: Action): Unit = assertEquals(
    MiddlewareStatus.Next(action),
    this,
    "Action expected to be passed!"
)

public fun List<MiddlewareStatus>.expectAllToBeConsumed(): Unit = assertEquals(
    map { MiddlewareStatus.Consumed },
    this,
    "All actions expected to be consumed!"
)

public fun List<MiddlewareStatus>.expectAllToBeNext(vararg actions: Action): Unit = assertEquals(
    actions.map(MiddlewareStatus::Next),
    this,
    "All actions expected to be passed!"
)

public fun List<MiddlewareStatus>.expectAllEquals(vararg status: MiddlewareStatus): Unit = assertEquals(
    status.toList(),
    this,
    "Middleware statuses not match!"
)