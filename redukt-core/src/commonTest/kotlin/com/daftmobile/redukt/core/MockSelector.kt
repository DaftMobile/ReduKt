package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.store.Selector

internal class MockSelector<T, R>(
    var onSelectorCall: Selector<T, R>
) {

    var receivedInput: T? = null
    var callsCounter = 0

    fun call(input: T): R {
        callsCounter++
        receivedInput = input
        return onSelectorCall(input)
    }
}