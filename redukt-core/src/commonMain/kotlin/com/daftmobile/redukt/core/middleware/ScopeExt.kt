package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope

public fun DispatchScope<*>.consume(): MiddlewareStatus.Consumed = MiddlewareStatus.Consumed

public fun DispatchScope<*>.next(action: Action): MiddlewareStatus.Next = MiddlewareStatus.Next(action)