package com.daftmobile.redukt.core.store.builder

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext

public interface ContextBuilderScope {
    public operator fun DispatchContext.unaryPlus()
}

internal class ContextBuilder : ContextBuilderScope {
    var context: DispatchContext = EmptyDispatchContext

    override operator fun DispatchContext.unaryPlus() {
        context += this
    }
}