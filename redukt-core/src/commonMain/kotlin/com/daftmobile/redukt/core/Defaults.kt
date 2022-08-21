package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

public fun defaultStoreClosure(
    coroutineScope: CoroutineScope = MainScope()
): DispatchClosure = DispatchCoroutineScope(coroutineScope)
