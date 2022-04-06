package com.github.lupuuss.redukt.core.middleware

import com.github.lupuuss.redukt.core.scope.DispatchScope

fun DispatchScope<*>.consume() = Middleware.Status.Consumed

fun DispatchScope<*>.pass() = Middleware.Status.Passed