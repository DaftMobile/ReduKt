package com.daftmobile.redukt.core.store.builder

import com.daftmobile.redukt.core.Middleware

public interface MiddlewaresBuilderScope<State> {
    public operator fun Middleware<State>.unaryPlus()
}

internal class MiddlewaresBuilder<State> : MiddlewaresBuilderScope<State> {
    val middlewares = mutableListOf<Middleware<State>>()

    override fun Middleware<State>.unaryPlus() {
        middlewares.add(this)
    }
}