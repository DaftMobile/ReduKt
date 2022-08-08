package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.scope.DispatchScope

public interface MiddlewareScope<State> : DispatchScope<State> {
    public suspend fun next(action: Action)
}

public inline fun MiddlewareScope<*>.dispatchFunction(noinline dispatch: DispatchFunction): DispatchFunction = dispatch

@PublishedApi
internal class MergedMiddlewareScope<State>(
    dispatchScope: DispatchScope<State>,
    private val nextFunction: DispatchFunction
): MiddlewareScope<State>, DispatchScope<State> by dispatchScope {

    override suspend fun next(action: Action) = nextFunction(action)
}