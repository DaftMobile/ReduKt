package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.log.util.jsToString

fun interface ActionFormatter {
    fun format(action: Action): String

    companion object {
        /**
         * Uses [Any.toString] to transform action to [String].
         */
        fun byToString() = ActionFormatter { it.toString() }


        /**
         * On JS platform improved toString is used to avoid 'object Object'. It prints JS class name instead.
         * On other platform it behaves just like [byToString].
         */
        fun byJsToString() = ActionFormatter { it.jsToString(false) }

        /**
         * On JS platform improved toString is used to avoid 'object Object'. It prints JS class name with object identifier.
         * On other platform it behaves just like [byToString].
         */
        fun byRefJsToString() = ActionFormatter { it.jsToString(true) }
    }
}