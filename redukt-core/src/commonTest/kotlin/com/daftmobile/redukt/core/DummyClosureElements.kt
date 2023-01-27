package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

internal class ClosureElementA : DispatchClosure.Element {
    override val key = Key

    companion object Key : DispatchClosure.Key<ClosureElementA>
}

internal class ClosureElementB : DispatchClosure.Element {
    override val key = Key

    companion object Key : DispatchClosure.Key<ClosureElementB>
}

internal class ClosureElementC : DispatchClosure.Element {
    override val key = Key

    companion object Key : DispatchClosure.Key<ClosureElementC>
}