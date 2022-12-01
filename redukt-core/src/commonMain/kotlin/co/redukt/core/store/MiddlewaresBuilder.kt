package co.redukt.core.store

import co.redukt.core.middleware.Middleware

/**
 * The scope for building a middlewares pipeline.
 */
public interface MiddlewaresBuilderScope<State> {

    /**
     * Adds [this] middleware to a resulting middlewares pipeline.
     */
    public operator fun Middleware<State>.unaryPlus()
}

internal class MiddlewaresBuilder<State> : MiddlewaresBuilderScope<State> {
    val middlewares = mutableListOf<Middleware<State>>()

    override fun Middleware<State>.unaryPlus() {
        middlewares.add(this)
    }
}