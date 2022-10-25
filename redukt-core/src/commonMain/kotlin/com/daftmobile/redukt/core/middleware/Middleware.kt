package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.DispatchScope

public typealias Middleware<State> = MiddlewareScope<State>.() -> DispatchFunction

public interface MiddlewareScope<out State> : DispatchScope<State> {
    public fun next(action: Action)
}

@PublishedApi
internal class MergedMiddlewareScope<State>(
    dispatchScope: DispatchScope<State>,
    private val nextFunction: DispatchFunction
): MiddlewareScope<State>, DispatchScope<State> by dispatchScope {

    override fun next(action: Action) = nextFunction(action)
}
