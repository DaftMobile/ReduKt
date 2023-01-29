package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.store.select.SelectorFunction

internal class MockSelector<T, R>(
    var onSelectorCall: SelectorFunction<T, R>
) {

    var receivedInput: T? = null
    var callsCounter = 0

    fun call(input: T): R {
        callsCounter++
        receivedInput = input
        return onSelectorCall(input)
    }
}