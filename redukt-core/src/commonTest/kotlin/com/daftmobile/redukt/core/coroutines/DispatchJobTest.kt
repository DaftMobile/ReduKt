package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.closure.*
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.sequences.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

private object TestForegroundJobAction : ForegroundJobAction

@OptIn(ExperimentalCoroutinesApi::class)
class DispatchJobTest {

    private val dispatchCoroutineScope = DispatchCoroutineScope(TestScope())
    private val registry = SingleForegroundJobRegistry()

    private var dispatchFunction: DispatchFunction = { }
    private var closure: DispatchClosure = LocalClosure { EmptyDispatchClosure }

    private val scope by lazy { dispatchScope(closure = closure, dispatch = dispatchFunction, getState = { }) }

    @Test
    fun launchForegroundShouldRegisterJobInLocalJobRegistry() {
        closure = LocalClosure { registry + dispatchCoroutineScope }
        val job = scope.launchForeground { }
        registry.consumeOrNull() shouldBe job
    }

    @Test
    fun launchForegroundShouldLaunchJobInLocalDispatchCoroutineScope() {
        closure = LocalClosure { registry + dispatchCoroutineScope }
        val job = scope.launchForeground { }
        dispatchCoroutineScope.coroutineContext.job.children shouldContain job
    }

    @Test
    fun launchInForegroundShouldLaunchForegroundJob() = runTest {
        closure = LocalClosure { registry + DispatchCoroutineScope(this) }
        val job = flowOf(1, 2, 3).launchInForeground(scope)
        registry.consumeOrNull() shouldBe job
    }

    @Test
    fun launchInForegroundShouldCollectFlowUsingLaunchForeground() = runTest {
        closure = LocalClosure { registry + DispatchCoroutineScope(this) }
        val elements = mutableListOf<Int>()
        val job = flowOf(1, 2, 3)
            .onEach(elements::add)
            .launchInForeground(scope)
        job.join()
        elements shouldBe listOf(1, 2, 3)
    }

    @Test
    fun dispatchJobShouldNotFailOnLaunchForeground() {
        closure = LocalClosure { dispatchCoroutineScope }
        dispatchFunction = { scope.launchForeground { } }
        shouldNotThrowAny { scope.dispatchJob(TestForegroundJobAction) }
    }

    @Test
    fun dispatchJobShouldThrowIllegalArgumentExceptionWhenForegroundJobNotRegistered() {
        closure = LocalClosure { dispatchCoroutineScope }
        dispatchFunction = { }
        shouldThrow<IllegalArgumentException> { scope.dispatchJob(TestForegroundJobAction) }
    }

    @Test
    fun dispatchJobShouldReturnRegisteredJob() {
        closure = LocalClosure { dispatchCoroutineScope }
        var registeredJob: Job? = null
        dispatchFunction = { registeredJob = scope.launchForeground { } }
        scope.dispatchJob(TestForegroundJobAction) shouldBe registeredJob
    }

    @Test
    fun dispatchJobInShouldNotFailOnLaunchForeground() {
        dispatchFunction = { scope.launchForeground { } }
        shouldNotThrowAny { scope.dispatchJobIn(TestForegroundJobAction, dispatchCoroutineScope) }
    }

    @Test
    fun dispatchJobInShouldThrowIllegalArgumentExceptionWhenForegroundJobNotRegistered() {
        dispatchFunction = { }
        shouldThrow<IllegalArgumentException> { scope.dispatchJobIn(TestForegroundJobAction, dispatchCoroutineScope) }
    }

    @Test
    fun dispatchJobInShouldProvideLocalDispatchCoroutineScope() {
        dispatchFunction = {
            scope.launchForeground {  }
            scope.localClosure.find(DispatchCoroutineScope) shouldNotBe null
        }
        scope.dispatchJobIn(TestForegroundJobAction, TestScope())
    }

    @Test
    fun joinDispatchJobShouldProperlyJoinLaunchedCoroutine() = runTest {
        var joined = false
        dispatchFunction = {
            scope.launchForeground {
                delay(1_000)
                joined = true
            }
        }
        scope.joinDispatchJob(TestForegroundJobAction)
        joined shouldBe true
    }
}