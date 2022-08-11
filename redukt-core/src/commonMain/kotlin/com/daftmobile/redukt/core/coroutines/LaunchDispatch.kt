package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.ActionDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public fun ActionDispatcher.launchDispatch(action: Action): Job = closure[StoreCoroutineScope].launch {dispatch(action) }