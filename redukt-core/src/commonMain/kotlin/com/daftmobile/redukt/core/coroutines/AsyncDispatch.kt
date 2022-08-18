package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.LocalClosureScope
import com.daftmobile.redukt.core.SuspendAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public fun LocalClosureScope.launchForeground(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = coroutineScope
    .launch(context, start, block)
    .also(foregroundJobRegistry::register)

public fun DispatchScope<*>.asyncDispatch(action: SuspendAction): Job {
    val registry = DefaultForegroundJobRegistry()
    dispatch(action, registry)
    return registry.consume()
}

public suspend inline fun DispatchScope<*>.awaitDispatch(action: SuspendAction): Unit = asyncDispatch(action).join()