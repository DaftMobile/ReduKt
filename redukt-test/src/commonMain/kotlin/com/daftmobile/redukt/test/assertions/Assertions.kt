package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.tools.pullOrNull
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

public fun ActionsAssertScope.expectActionEquals(action: Action) {
    requiresAtLeasOneAction()
    assertEquals(pipeline.pull(), action, "Expect current action to be equal to $action. $stackDescription")
}

public inline fun <reified T : Action> ActionsAssertScope.expectActionOfType(checks: (T) -> Unit = {}) {
    requiresAtLeasOneAction()
    val action = pipeline.pull()
    assertIs<T>(action, "Expected action is not an instance of type ${T::class.simpleName}!").apply(checks)
}

public inline fun <reified T : Action> ActionsAssertScope.expectSingleActionOfType(checks: (T) -> Unit = {}) {
    requiresAtLeasOneAction()
    val action = pipeline.pull()
    assertIs<T>(action, "Expected action is not an instance of type ${T::class.simpleName}!").apply(checks)
    requiresSingle()
}

public fun ActionsAssertScope.expectSingleActionEquals(action: Action) {
    requiresAtLeasOneAction()
    assertEquals(pipeline.pull(), action, "Expect single action to be equal to $action. $stackDescription")
    requiresSingle()
}

public fun ActionsAssertScope.expectEvery(message: String? = null, actionCheck: (Action) -> Boolean) {
    while (true) {
        val action = pipeline.pullOrNull() ?: return
        assertTrue(
            actionCheck(action),
            (message
                ?: "Expected every action to fulfill specified conditions but $action have not!") + stackDescription
        )
    }
}

public fun ActionsAssertScope.expectAllActionsCount(count: Int): Unit = assertEquals(
    count,
    history.size,
    "Expected all dispatched actions count to be equal $count!"
)

public fun ActionsAssertScope.expectNoActions(): Unit = assertTrue(
    history.isEmpty(),
    "Expected no actions! $stackDescription"
)

public fun ActionsAssertScope.expectNoMoreActions() {
    assertTrue(pipeline.isEmpty(), "Expected all actions to be processed! $stackDescription")
}

@PublishedApi
internal fun ActionsAssertScope.requiresAtLeasOneAction() {
    assertTrue(
        pipeline.isNotEmpty(),
        "This assertion requires at least one action to process, but no actions left to process! $stackDescription",
    )
}

@PublishedApi
internal fun ActionsAssertScope.requiresSingle() {
    assertTrue(
        pipeline.isEmpty(),
        "Expected single action, but more actions have been dispatched! $stackDescription"
    )
}