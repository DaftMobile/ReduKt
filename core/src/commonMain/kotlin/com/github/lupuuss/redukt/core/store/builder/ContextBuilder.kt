package com.github.lupuuss.redukt.core.store.builder

import com.github.lupuuss.redukt.core.context.DispatchContext
import com.github.lupuuss.redukt.core.context.EmptyDispatchContext

interface ContextBuilderScope {
    operator fun DispatchContext.unaryPlus()
}

internal class ContextBuilder : ContextBuilderScope {
    var context: DispatchContext = EmptyDispatchContext

    override operator fun DispatchContext.unaryPlus() {
        context += this
    }
}