package com.daftmobile.redukt.test.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.test.assertions.ActionsAssertScope

interface MiddlewareTesterScope<State> : ActionsAssertScope {

    var state: State

    var dispatchContext: DispatchContext

    fun onAction(action: Action): Middleware.Status

    fun onAllActions(vararg actions: Action): List<Middleware.Status>
}