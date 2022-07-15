package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action
import kotlin.reflect.KClass

public fun interface LogFilter {
    public fun filter(action: Action): Boolean

    public companion object {

        public fun all(): LogFilter = LogFilter { true }

        public inline fun <reified T : Any> ofType(): LogFilter = LogFilter { it is T }

        public fun ofTypes(vararg types: KClass<*>): LogFilter = LogFilter { it::class in types }
    }
}