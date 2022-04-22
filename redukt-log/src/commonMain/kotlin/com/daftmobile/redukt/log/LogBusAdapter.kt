package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action

fun interface LogBusAdapter {

    fun log(action: Action)

    companion object {
        fun systemOut(tag: String? = null) = LogBusAdapter { println("${tag.orEmpty()}$it") }
    }
}