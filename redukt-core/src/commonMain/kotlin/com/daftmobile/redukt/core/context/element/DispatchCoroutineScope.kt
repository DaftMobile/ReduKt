package com.daftmobile.redukt.core.context.element

import com.daftmobile.redukt.core.ActionDispatcher
import com.daftmobile.redukt.core.context.DispatchContext
import kotlinx.coroutines.CoroutineScope

class DispatchCoroutineScope(scope: CoroutineScope) : DispatchContext.Element, CoroutineScope by scope {
    override val key get() = Key

    companion object Key : DispatchContext.Key<DispatchCoroutineScope>
}

inline val ActionDispatcher.coroutineScope get() = dispatchContext[DispatchCoroutineScope]