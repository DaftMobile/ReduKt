package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action
import kotlin.reflect.KClass

fun interface LogFilter {
    fun filter(action: Action): Boolean

    companion object {

        fun all() = LogFilter { true }

        inline fun <reified T : Any> ofType() = LogFilter { it is T }

        fun ofTypes(vararg types: KClass<*>) = LogFilter { it::class in types }
    }
}