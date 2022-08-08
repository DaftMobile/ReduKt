package com.daftmobile.redukt.core.context.element

import com.daftmobile.redukt.core.ActionDispatcher
import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.scope.DispatchScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext

public class DispatchCoroutineScope(
    scope: CoroutineScope = MainScope()
) : DispatchContext.Element, CoroutineScope by scope {
    public override val key: Key = Key

    public companion object Key : DispatchContext.Key<DispatchCoroutineScope>
}

public inline val ActionDispatcher.coroutineScope: CoroutineScope get() = dispatchContext[DispatchCoroutineScope]

public suspend inline fun <T> DispatchScope<*>.withDispatchCoroutineContext(
    noinline block: suspend CoroutineScope.() -> T
): T = withContext(coroutineScope.coroutineContext, block)