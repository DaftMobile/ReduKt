package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.tools.Queue
import com.daftmobile.redukt.test.tools.emptyQueue

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