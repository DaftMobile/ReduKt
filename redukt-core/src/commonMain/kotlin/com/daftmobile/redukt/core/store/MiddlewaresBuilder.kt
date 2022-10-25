package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.middleware.Middleware

/**
 * The scope for building a middlewares list.
 */
public interface MiddlewaresBuilderScope<State> {

    /**
     * Adds [this] middleware to a resulting middlewares list.
     */
    public operator fun Middleware<State>.unaryPlus()
}

internal class MiddlewaresBuilder<State> : MiddlewaresBuilderScope<State> {
    val middlewares = mutableListOf<Middleware<State>>()

    override fun Middleware<State>.unaryPlus() {
        middlewares.add(this)
    }
}