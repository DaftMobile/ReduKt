package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.closure.DispatchClosure

public typealias Middleware<State> = MiddlewareScope<State>.() -> DispatchFunction

public interface MiddlewareScope<State> : DispatchScope<State> {
    public fun next(action: Action)

    @DelicateReduKtApi
    public fun next(action: Action, closure: DispatchClosure)
}

@PublishedApi
internal class MergedMiddlewareScope<State>(
    dispatchScope: DispatchScope<State>,
    private val nextFunction: DispatchFunction
): MiddlewareScope<State>, DispatchScope<State> by dispatchScope {

    private val defaultLocalScope = closure.asLocalScope()

    override fun next(action: Action) = defaultLocalScope.nextFunction(action)

    @DelicateReduKtApi
    override fun next(action: Action, closure: DispatchClosure) = (this.closure + closure)
        .asLocalScope()
        .nextFunction(action)
}