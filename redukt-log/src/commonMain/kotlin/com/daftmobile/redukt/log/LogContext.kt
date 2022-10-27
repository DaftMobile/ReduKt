package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchScope

public data class LogContext<out State>(
    val scope: DispatchScope<State>,
    val action: Action,
)