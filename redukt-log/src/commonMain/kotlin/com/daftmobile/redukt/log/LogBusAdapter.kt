package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action

public fun interface LogBusAdapter {

    public fun log(action: Action)

    public companion object {
        public fun systemOut(
            tag: String? = null,
            actionFormatter: ActionFormatter = ActionFormatter.byJsToString()
        ): LogBusAdapter = LogBusAdapter { action ->
            actionFormatter.format(action).split("\n").forEach {
                println("${tag.orEmpty()}$it")
            }
        }
    }
}