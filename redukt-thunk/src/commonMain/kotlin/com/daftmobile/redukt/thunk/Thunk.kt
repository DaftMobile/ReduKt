package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import kotlinx.coroutines.CancellableContinuation

sealed class Thunk<T> : Action {
    internal var continuation: CancellableContinuation<T>? = null
}