package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope


typealias Middleware<State> = suspend DispatchScope<State>.(action: Action) -> MiddlewareStatus

sealed class MiddlewareStatus {
    object Consumed : MiddlewareStatus()

    data class Next(val action: Action) : MiddlewareStatus()
}