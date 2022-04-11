package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.context.DispatchContext

class TestDispatchContext : DispatchContext.Element {
    override val key = Key

    companion object Key : DispatchContext.Key<TestDispatchContext>
}