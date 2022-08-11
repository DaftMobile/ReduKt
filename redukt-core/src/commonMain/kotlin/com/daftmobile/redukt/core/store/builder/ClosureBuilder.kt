package com.daftmobile.redukt.core.store.builder

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure

public interface ClosureBuilderScope {
    public operator fun DispatchClosure.unaryPlus()
}

internal class ClosureBuilder : ClosureBuilderScope {
    var closure: DispatchClosure = EmptyDispatchClosure

    override operator fun DispatchClosure.unaryPlus() {
        closure += this
    }
}