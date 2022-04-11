package com.daftmobile.redukt.core.context.element

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.scope.DispatchScope
import kotlinx.coroutines.CoroutineScope

class DispatchCoroutineScope(scope: CoroutineScope) : DispatchContext.Element, CoroutineScope by scope {
    override val key get() = DispatchCoroutineScope

    companion object Key : DispatchContext.Key<DispatchCoroutineScope>
}

val DispatchScope<*>.coroutineScope get() = dispatchContext[DispatchCoroutineScope]