package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public fun DispatchScope<*>.launchDispatch(action: Action): Job = closure[StoreCoroutineScope].launch {dispatch(action) }