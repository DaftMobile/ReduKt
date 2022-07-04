package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope

fun DispatchScope<*>.consume() = MiddlewareStatus.Consumed

fun DispatchScope<*>.next(action: Action) = MiddlewareStatus.Next(action)