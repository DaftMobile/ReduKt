package com.daftmobile.redukt.core

interface ActionDispatcher {
    fun dispatch(action: Action)
}

fun ActionDispatcher.dispatchIfPresent(action: Action?) = action?.let(::dispatch)