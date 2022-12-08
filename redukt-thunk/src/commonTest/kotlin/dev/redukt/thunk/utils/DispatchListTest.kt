package dev.redukt.thunk.utils

import dev.redukt.core.Action
import dev.redukt.test.assertions.expectActionsSequence
import dev.redukt.test.assertions.expectNoMoreActions
import dev.redukt.test.thunk.testExecute
import dev.redukt.thunk.utils.DispatchListSupport.JoiningCoroutines
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class DispatchListTest {

    private object ActionA : Action
    private object ActionB : Action
    private object ActionC : Action

    private val actionsList = listOf(ActionA, ActionB, ActionC)

    @Test
    fun shouldDispatchPassedActionsWithGivenOrder() = DispatchList(actionsList).testExecute(Unit) {
        expectActionsSequence(ActionA, ActionB, ActionB)
        expectNoMoreActions()
    }

    @Test
    fun plusOperatorShouldCreateMultipleDispatchFromActions() {
        ActionA + ActionB + ActionC shouldBe DispatchList(actionsList)
    }

    @Test
    fun supportShouldCreateJoiningCoroutinesDispatchList() {
        val result = ActionA + ActionB + ActionC support JoiningCoroutines(concurrent = true)
        result shouldBe JoiningCoroutinesDispatchList(actionsList, true)
    }
}