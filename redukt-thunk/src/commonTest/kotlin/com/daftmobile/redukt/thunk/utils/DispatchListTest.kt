package com.daftmobile.redukt.thunk.utils

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.assertions.assertActionSequence
import com.daftmobile.redukt.test.thunk.tester
import com.daftmobile.redukt.thunk.utils.DispatchListSupport.JoiningCoroutines
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class DispatchListTest {

    private object ActionA : Action
    private object ActionB : Action
    private object ActionC : Action

    private val actionsList = listOf(ActionA, ActionB, ActionC)

    private val tester = DispatchList(actionsList).tester(Unit)

    @Test
    fun plusOperatorShouldCreateMultipleDispatchFromActions() {
        ActionA + ActionB + ActionC shouldBe DispatchList(actionsList)
    }

    @Test
    fun supportShouldCreateJoiningCoroutinesDispatchList() {
        val result = ActionA + ActionB + ActionC support JoiningCoroutines(concurrent = true)
        result shouldBe JoiningCoroutinesDispatchList(actionsList, true)
    }

    @Test
    fun shouldDispatchPassedActionsWithGivenOrder() = tester.test {
        testExecute()
        assertActionSequence(ActionA, ActionB, ActionC)
    }

}
