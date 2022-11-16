package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.store.PureSelector

internal class MockSelector<T, R>(
    var onSelectorCall: PureSelector<T, R>
) {

    var receivedInput: T? = null
    var callsCounter = 0

    fun call(input: T): R {
        callsCounter++
        receivedInput = input
        return onSelectorCall(input)
    }
}