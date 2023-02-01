package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.closure.*
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.sequences.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

private object TestForegroundJobAction : ForegroundJobAction

@OptIn(ExperimentalCoroutinesApi::class)
class DispatchJobTest {

    private val testScope = TestScope()
    private val dispatchCoroutineScope = DispatchCoroutineScope(testScope)
    private val registry = SingleForegroundJobRegistry()

    private val initialCoroutineScope = DispatchCoroutineScope(Dispatchers.Unconfined)

    private var dispatchFunction: DispatchFunction = { }
    private var closure: DispatchClosure = initialCoroutineScope + LocalClosureContainer()

    private val dispatchScope by lazy { dispatchScope(closure = closure, dispatch = dispatchFunction, getState = { }) }

    @Test
    fun launchForegroundShouldRegisterJobInLocalJobRegistry() {
        closure.withLocalClosure(registry + dispatchCoroutineScope) {
            val job = dispatchScope.launchForeground { }
            registry.consumeOrNull() shouldBe job
        }
    }

    @Test
    fun launchForegroundShouldLaunchJobInLocalDispatchCoroutineScope() {
        closure.withLocalClosure(registry + dispatchCoroutineScope) {
            val job = dispatchScope.launchForeground { }
            dispatchCoroutineScope.coroutineContext.job.children shouldContain job
        }
    }

    @Test
    fun launchForegroundShouldNotLeakLocalClosureToTheCoroutineIfCoroutineIsLaunchedImmediately() {
        var localClosureFromTheCoroutine: DispatchClosure? = null
        closure.withLocalClosure(registry + dispatchCoroutineScope) {
            closure.launchForeground(start = CoroutineStart.UNDISPATCHED) {
                localClosureFromTheCoroutine = local
            }
        }
        localClosureFromTheCoroutine?.get(DispatchCoroutineScope) shouldBe initialCoroutineScope
    }

    @Test
    fun launchInForegroundShouldLaunchForegroundJob() {
        closure.withLocalClosure(registry + dispatchCoroutineScope) {
            val job = flowOf(1, 2, 3).launchInForegroundOf(dispatchScope)
            registry.consumeOrNull() shouldBe job
        }
    }

    @Test
    fun launchInForegroundShouldCollectFlowUsingLaunchForeground() = runTest {
        closure.withLocalClosure(registry + DispatchCoroutineScope(this)) {
            val elements = mutableListOf<Int>()
            val job = flowOf(1, 2, 3)
                .onEach(elements::add)
                .launchInForegroundOf(dispatchScope)
            job.join()
            elements shouldBe listOf(1, 2, 3)
        }
    }

    @Test
    fun dispatchJobShouldNotFailOnLaunchForeground() {
        closure.withLocalClosure(dispatchCoroutineScope) {
            dispatchFunction = { dispatchScope.launchForeground { } }
            shouldNotThrowAny { dispatchScope.dispatchJob(TestForegroundJobAction) }
        }
    }

    @Test
    fun dispatchJobShouldThrowIllegalArgumentExceptionWhenForegroundJobNotRegistered() {
        closure.withLocalClosure(dispatchCoroutineScope) {
            dispatchFunction = { }
            shouldThrow<IllegalArgumentException> { dispatchScope.dispatchJob(TestForegroundJobAction) }
        }
    }

    @Test
    fun dispatchJobShouldReturnRegisteredJob() {
        closure.withLocalClosure(dispatchCoroutineScope) {
            var registeredJob: Job? = null
            dispatchFunction = { registeredJob = dispatchScope.launchForeground { } }
            dispatchScope.dispatchJob(TestForegroundJobAction) shouldBe registeredJob
        }
    }

    @Test
    fun dispatchJobInShouldNotFailOnLaunchForeground() {
        dispatchFunction = { dispatchScope.launchForeground { } }
        shouldNotThrowAny { dispatchScope.dispatchJobIn(TestForegroundJobAction, dispatchCoroutineScope) }
    }

    @Test
    fun dispatchJobInShouldThrowIllegalArgumentExceptionWhenForegroundJobNotRegistered() {
        dispatchFunction = { }
        shouldThrow<IllegalArgumentException> { dispatchScope.dispatchJobIn(TestForegroundJobAction, dispatchCoroutineScope) }
    }

    @Test
    fun dispatchJobInShouldProvideLocalDispatchCoroutineScope() {
        dispatchFunction = {
            dispatchScope.launchForeground {  }
            dispatchScope.localClosure.find(DispatchCoroutineScope) shouldNotBe null
        }
        dispatchScope.dispatchJobIn(TestForegroundJobAction, TestScope())
    }

    @Test
    fun joinDispatchJobShouldProperlyJoinLaunchedCoroutine() = runTest {
        var joined = false
        dispatchFunction = {
            dispatchScope.launchForeground {
                delay(1_000)
                joined = true
            }
        }
        dispatchScope.joinDispatchJob(TestForegroundJobAction)
        joined shouldBe true
    }
}