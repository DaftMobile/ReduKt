package dev.redukt.thunk.utils

import dev.redukt.core.coroutines.ForegroundJobAction
import dev.redukt.core.coroutines.launchForeground
import dev.redukt.test.assertions.assertActionEquals
import dev.redukt.test.assertions.assertActionSequence
import dev.redukt.test.assertions.assertNoMoreActions
import dev.redukt.test.thunk.tester
import dev.redukt.test.tools.RunningCoroutinesClosure
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class JoiningCoroutinesDispatchListTest {

    private object ActionA : ForegroundJobAction
    private object ActionB : ForegroundJobAction
    private object ActionC : ForegroundJobAction

    private val actionsList = listOf(ActionA, ActionB, ActionC)

    private val tester = JoiningCoroutinesDispatchList(actionsList, concurrent = false).tester(Unit)
    @Test
    fun shouldDispatchPassedActionsWithGivenOrder() = runTest {
        tester.test {
            testExecute()
            assertActionSequence(ActionA, ActionB, ActionC)
        }
    }

    @Test
    fun shouldAwaitForegroundCoroutineBeforeDispatchingNextActions() = runTest(dispatchTimeoutMs = 2_000) {
        tester.test {
            val channel = Channel<Unit>()
            closure += RunningCoroutinesClosure()
            onDispatch {
                if (it is ForegroundJobAction) closure.launchForeground { channel.send(Unit) }
            }
            launch { testExecute() }
            channel.receive()
            assertActionEquals(ActionA)
            assertNoMoreActions()
            channel.receive()
            assertActionEquals(ActionB)
            assertNoMoreActions()
            channel.receive()
            assertActionEquals(ActionC)
            assertNoMoreActions()
        }
    }

    @Test
    fun shouldAwaitForegroundCoroutinesBeforeCompletionIfConcurrent() = runTest(dispatchTimeoutMs = 2_000) {
        val concurrentDispatchListTester = JoiningCoroutinesDispatchList(actionsList, concurrent = true).tester(Unit)
        concurrentDispatchListTester.test {
            val channel = Channel<Int>()
            closure += RunningCoroutinesClosure()
            var i = 1
            onDispatch {
                if (it is ForegroundJobAction) closure.launchForeground { channel.send(i++) }
            }
            launch {
                testExecute()
                channel.send(4)
                channel.close()
            }
            yield()
            assertActionSequence(ActionA, ActionB, ActionC)
            channel.consumeAsFlow().toList() shouldBe listOf(1, 2, 3, 4)
        }
    }
}