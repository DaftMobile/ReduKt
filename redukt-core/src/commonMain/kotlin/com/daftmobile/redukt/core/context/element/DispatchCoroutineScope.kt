package com.daftmobile.redukt.core.context.element

import com.daftmobile.redukt.core.ActionDispatcher
import com.daftmobile.redukt.core.context.DispatchContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

public class DispatchCoroutineScope(
    scope: CoroutineScope = MainScope()
) : DispatchContext.Element, CoroutineScope by scope {
    public override val key: Key = Key

    public companion object Key : DispatchContext.Key<DispatchCoroutineScope>
}

public inline val ActionDispatcher.coroutineScope: CoroutineScope get() = dispatchContext[DispatchCoroutineScope]