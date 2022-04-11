package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.ActionDispatcher
import com.daftmobile.redukt.core.context.DispatchContext

interface DispatchScope<State> : ActionDispatcher {
    val dispatchContext: DispatchContext

    val state: State
}