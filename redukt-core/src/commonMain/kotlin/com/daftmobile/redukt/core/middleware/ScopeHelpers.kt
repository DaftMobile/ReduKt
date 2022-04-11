package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.scope.DispatchScope

fun DispatchScope<*>.consume() = Middleware.Status.Consumed

fun DispatchScope<*>.pass() = Middleware.Status.Passed