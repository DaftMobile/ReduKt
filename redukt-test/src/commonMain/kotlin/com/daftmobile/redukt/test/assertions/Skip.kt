package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.tools.pullOrNull
import kotlin.test.fail

fun ActionsAssertScope.skipOneAction() = skipActions(1)

fun ActionsAssertScope.skipActions(count: Int) = repeat(count) { pipeline.pullOrNull() ?: fail("No action to skip!") }

fun ActionsAssertScope.skipActionsWhile(action: (Action) -> Boolean) {
    while (pipeline.firstOrNull()?.let(action) == true) pipeline.pull()
}

fun ActionsAssertScope.skipOtherActions() = skipActionsWhile { true }