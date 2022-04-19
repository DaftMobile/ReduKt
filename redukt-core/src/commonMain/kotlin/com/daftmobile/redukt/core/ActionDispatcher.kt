package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.context.DispatchContext

interface ActionDispatcher {

    val dispatchContext: DispatchContext

    fun dispatch(action: Action)
}

fun ActionDispatcher.dispatchIfPresent(action: Action?) = action?.let(::dispatch)