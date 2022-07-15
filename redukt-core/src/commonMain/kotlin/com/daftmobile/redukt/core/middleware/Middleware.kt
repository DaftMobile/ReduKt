package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope


public typealias Middleware<State> = suspend DispatchScope<State>.(action: Action) -> MiddlewareStatus

public sealed class MiddlewareStatus {
    public object Consumed : MiddlewareStatus()

    public data class Next(val action: Action) : MiddlewareStatus()
}