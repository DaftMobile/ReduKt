package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.middleware.Middleware

public fun <State> Middleware<State>.tester(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure
): MiddlewareTester<State> = MiddlewareTester(this, initialState, initialClosure)