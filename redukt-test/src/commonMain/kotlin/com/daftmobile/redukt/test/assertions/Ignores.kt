package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.tools.pullOrNull
import kotlin.test.fail

fun ActionsAssertScope.ignore(count: Int = 1) = repeat(count) { pipeline.pullOrNull() ?: fail("No action to skip!") }

fun ActionsAssertScope.ignoreWhile(action: (Action) -> Boolean) {
    while (pipeline.pullOrNull()?.let(action) == true) Unit
}
