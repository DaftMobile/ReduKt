package com.github.lupuuss.redukt.core

import com.github.lupuuss.redukt.core.context.DispatchContext

class TestDispatchContext : DispatchContext.Element {
    override val key = Key

    companion object Key : DispatchContext.Key<TestDispatchContext>
}