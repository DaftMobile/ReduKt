package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.middleware.MiddlewareScope

public interface Action

public typealias DispatchFunction = suspend (action: Action) -> Unit

public typealias Reducer<T> = (T, Action) -> T

public typealias Middleware<State> = MiddlewareScope<State>.() -> DispatchFunction