package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer

/**
 * Returns a [DispatchClosure] that enables foreground coroutines mechanism.
 * */
@DelicateReduKtApi
public fun runningCoroutinesClosure(): DispatchClosure = EmptyForegroundJobRegistry + LocalClosureContainer()
