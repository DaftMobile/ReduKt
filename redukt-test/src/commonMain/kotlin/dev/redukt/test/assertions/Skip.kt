package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.pullOrNull
import kotlin.test.fail

public fun ActionsAssertScope.skipOneAction(): Unit = skipActions(1)

public fun ActionsAssertScope.skipActions(count: Int): Unit = repeat(count) {
    pipeline.pullOrNull() ?: fail("No action to skip!")
}

public fun ActionsAssertScope.skipActionsWhile(action: (Action) -> Boolean) {
    while (pipeline.firstOrNull()?.let(action) == true) pipeline.pull()
}

public fun ActionsAssertScope.skipOtherActions(): Unit = skipActionsWhile { true }