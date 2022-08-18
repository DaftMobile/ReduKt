package com.daftmobile.redukt.core.closure

import com.daftmobile.redukt.core.coroutines.EmptyForegroundJobRegistry
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope

public fun defaultStoreClosure(): DispatchClosure = DispatchCoroutineScope() + EmptyForegroundJobRegistry()