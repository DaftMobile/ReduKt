package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.core.middleware.Middleware

fun <State> Middleware<State>.tester(
    initialState: State,
    initialContext: DispatchContext = EmptyDispatchContext
) = MiddlewareTester(this, initialState, initialContext)