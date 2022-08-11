package com.daftmobile.redukt.core.closure

internal class ClosureElementA : DispatchClosure.Element {
    override val key = Key

    companion object Key : DispatchClosure.Key<ClosureElementA>
}

internal class ClosureElementB : DispatchClosure.Element {
    override val key = Key

    companion object Key : DispatchClosure.Key<ClosureElementB>
}
