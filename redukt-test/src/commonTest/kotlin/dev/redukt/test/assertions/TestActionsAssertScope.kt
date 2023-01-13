package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.Queue
import dev.redukt.test.tools.emptyQueue

internal class TestActionsAssertScope(
    override val unverified: Queue<Action>,
    override val history: List<Action>,
) : ActionsAssertScope {

    override fun clearActionsHistory() = Unit

}

internal fun testAssertion(
    unverified: Queue<Action> = emptyQueue(),
    history: List<Action> = unverified.toList(),
    block: ActionsAssertScope.() -> Unit
) {
    TestActionsAssertScope(unverified, history).apply(block)
}