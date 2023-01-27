package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer

/**
 * Returns a [DispatchClosure] that enables foreground coroutines mechanism. It requires launching
 * foreground coroutines manually inside [com.daftmobile.redukt.test.MutableDispatchScope.onDispatch] with `closure.launchForeground`.
 */
public fun RunningCoroutinesClosure(): DispatchClosure = LocalClosureContainer()