package com.daftmobile.redukt.core.context

internal class ContextElementA : DispatchContext.Element {
    override val key = Key

    companion object Key : DispatchContext.Key<ContextElementA>
}

internal class ContextElementB : DispatchContext.Element {
    override val key = Key

    companion object Key : DispatchContext.Key<ContextElementB>
}
