package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action

fun interface LogBusAdapter {

    fun log(action: Action)

    companion object {
        fun systemOut(tag: String? = null) = LogBusAdapter { action ->
            action.toString().split("\n").forEach {
                println("${tag.orEmpty()}$it")
            }
        }
    }
}