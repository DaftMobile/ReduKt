package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.middleware.Middleware
import kotlin.test.assertEquals

fun Middleware.Status.expectToBeConsumed() = assertEquals(
    Middleware.Status.Consumed,
    this,
    "Action expected to be consumed!"
)

fun Middleware.Status.expectToBePassed() = assertEquals(
    Middleware.Status.Passed,
    this,
    "Action expected to be passed!"
)

fun List<Middleware.Status>.expectAllToBeConsumed() = assertEquals(
    map { Middleware.Status.Consumed },
    this,
    "All actions expected to be consumed!"
)

fun List<Middleware.Status>.expectAllToBePassed() = assertEquals(
    map { Middleware.Status.Passed },
    this,
    "All actions expected to be passed!"
)

fun List<Middleware.Status>.expectAllEquals(vararg status: Middleware.Status) = assertEquals(
    status.toList(),
    this,
    "Middleware statuses not match!"
)