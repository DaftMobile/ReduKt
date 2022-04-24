package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action

class Log(private val message: Any) : Action {
    override fun toString(): String = message.toString()
}