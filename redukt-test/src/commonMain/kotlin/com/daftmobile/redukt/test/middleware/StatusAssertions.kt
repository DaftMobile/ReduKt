package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.middleware.MiddlewareStatus
import kotlin.test.assertEquals

fun MiddlewareStatus.expectToBeConsumed() = assertEquals(
    MiddlewareStatus.Consumed,
    this,
    "Action expected to be consumed!"
)

fun MiddlewareStatus.expectToBeNext(action: Action) = assertEquals(
    MiddlewareStatus.Next(action),
    this,
    "Action expected to be passed!"
)

fun List<MiddlewareStatus>.expectAllToBeConsumed() = assertEquals(
    map { MiddlewareStatus.Consumed },
    this,
    "All actions expected to be consumed!"
)

fun List<MiddlewareStatus>.expectAllToBeNext(vararg actions: Action) = assertEquals(
    actions.map(MiddlewareStatus::Next),
    this,
    "All actions expected to be passed!"
)

fun List<MiddlewareStatus>.expectAllEquals(vararg status: MiddlewareStatus) = assertEquals(
    status.toList(),
    this,
    "Middleware statuses not match!"
)