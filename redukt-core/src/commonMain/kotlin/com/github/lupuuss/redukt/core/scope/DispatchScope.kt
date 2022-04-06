package com.github.lupuuss.redukt.core.scope

import com.github.lupuuss.redukt.core.ActionDispatcher
import com.github.lupuuss.redukt.core.context.DispatchContext

interface DispatchScope<State> : ActionDispatcher {
    val dispatchContext: DispatchContext

    val state: State
}