package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

class TestDispatchClosure : DispatchClosure.Element {
    override val key = Key

    companion object Key : DispatchClosure.Key<TestDispatchClosure>
}