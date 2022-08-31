package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DispatchScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public fun DispatchScope<*>.synchronized(block: suspend DispatchScope<*>.() -> Unit): Job = coroutineScope.launch {
    block()
}
