package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DispatchScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Runs given [block] as a coroutine in [DispatchScope.coroutineScope].
 * Effectively it ensures that any operation on a [DispatchScope] inside a [block] runs on proper thread.
 */
public fun DispatchScope<*>.synchronized(block: suspend DispatchScope<*>.() -> Unit): Job = coroutineScope.launch {
    block()
}
