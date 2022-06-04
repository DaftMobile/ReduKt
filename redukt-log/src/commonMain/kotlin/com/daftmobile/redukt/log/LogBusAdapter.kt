package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action

fun interface LogBusAdapter {

    fun log(action: Action)

    companion object {
        fun systemOut(
            tag: String? = null,
            actionFormatter: ActionFormatter = ActionFormatter.byJsToString()
        ) = LogBusAdapter { action ->
            actionFormatter.format(action).split("\n").forEach {
                println("${tag.orEmpty()}$it")
            }
        }
    }
}