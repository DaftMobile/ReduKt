package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope

public fun defaultStoreClosure(): DispatchClosure = DispatchCoroutineScope()
