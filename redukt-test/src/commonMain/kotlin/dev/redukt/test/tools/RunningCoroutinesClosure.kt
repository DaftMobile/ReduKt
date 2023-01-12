package dev.redukt.test.tools

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.LocalClosureContainer

/**
 * Returns a [DispatchClosure] that enables foreground coroutines mechanism. It requires launching
 * foreground coroutines manually inside [dev.redukt.test.MutableDispatchScope.onDispatch] with `closure.launchForeground`.
 */
public fun RunningCoroutinesClosure(): DispatchClosure = LocalClosureContainer()