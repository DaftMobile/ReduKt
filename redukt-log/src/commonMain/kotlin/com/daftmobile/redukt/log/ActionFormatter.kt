package com.daftmobile.redukt.log

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.log.util.jsToString

public fun interface ActionFormatter {
    public fun format(action: Action): String

    public companion object {
        /**
         * Uses [Any.toString] to transform action to [String].
         */
        public fun byToString(): ActionFormatter = ActionFormatter { it.toString() }


        /**
         * On JS platform improved toString is used to avoid 'object Object'. It prints JS class name instead.
         * On other platform it behaves just like [byToString].
         */
        public fun byJsToString(): ActionFormatter = ActionFormatter { it.jsToString(false) }

        /**
         * On JS platform improved toString is used to avoid 'object Object'. It prints JS class name with object identifier.
         * On other platform it behaves just like [byToString].
         */
        public fun byRefJsToString(): ActionFormatter = ActionFormatter { it.jsToString(true) }
    }
}