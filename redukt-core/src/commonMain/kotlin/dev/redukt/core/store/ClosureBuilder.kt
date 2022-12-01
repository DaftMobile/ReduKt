package dev.redukt.core.store

import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.EmptyDispatchClosure

/**
 * The scope for building a [DispatchClosure].
 */
public interface ClosureBuilderScope {

    /**
     * Accumulates [this] closure to a resulting one.
     */
    public operator fun DispatchClosure.unaryPlus()
}

internal class ClosureBuilder : ClosureBuilderScope {
    var closure: DispatchClosure = EmptyDispatchClosure

    override operator fun DispatchClosure.unaryPlus() {
        closure += this
    }
}