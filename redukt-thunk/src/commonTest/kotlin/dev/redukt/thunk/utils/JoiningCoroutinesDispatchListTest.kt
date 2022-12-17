package dev.redukt.thunk.utils

import dev.redukt.core.Action
import dev.redukt.test.assertions.assertActionSequence
import dev.redukt.test.assertions.assertNoMoreActions
import dev.redukt.test.thunk.testExecute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class JoiningCoroutinesDispatchListTest {

    private object ActionA : Action
    private object ActionB : Action
    private object ActionC : Action

    private val actionsList = listOf(ActionA, ActionB, ActionC)
    @Test
    fun shouldDispatchPassedActionsWithGivenOrder() = runTest {
        JoiningCoroutinesDispatchList(actionsList, true).testExecute(Unit) {
            assertActionSequence(ActionA, ActionB, ActionC)
            assertNoMoreActions()
        }
    }
}