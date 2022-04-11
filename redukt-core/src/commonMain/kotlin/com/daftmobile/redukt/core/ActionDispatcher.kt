package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.element.DispatchCoroutineScope
import kotlinx.coroutines.launch

interface ActionDispatcher {

    val dispatchContext: DispatchContext

    fun dispatch(action: Action)
}

fun ActionDispatcher.dispatchIfPresent(action: Action?) = action?.let(::dispatch)

fun ActionDispatcher.dispatchSynchronized(action: Action) = dispatchContext[DispatchCoroutineScope].launch { dispatch(action) }