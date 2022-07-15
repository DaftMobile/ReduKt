package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.tools.pullOrNull
import kotlin.test.fail

public fun ActionsAssertScope.skipOneAction(): Unit = skipActions(1)

public fun ActionsAssertScope.skipActions(count: Int): Unit = repeat(count) {
    pipeline.pullOrNull() ?: fail("No action to skip!")
}

public fun ActionsAssertScope.skipActionsWhile(action: (Action) -> Boolean) {
    while (pipeline.firstOrNull()?.let(action) == true) pipeline.pull()
}

public fun ActionsAssertScope.skipOtherActions(): Unit = skipActionsWhile { true }