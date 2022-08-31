package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.CoreDispatchScope
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.JobAction
import com.daftmobile.redukt.core.closure.CoreLocalClosure
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.localClosure
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

private object TestJobAction : JobAction

@OptIn(ExperimentalCoroutinesApi::class)
class DispatchJobTest {

    private val dispatchCoroutineScope = DispatchCoroutineScope(TestScope())
    private val registry = SingleForegroundJobRegistry()

    private var dispatchFunction: DispatchFunction = { }
    private var closure: DispatchClosure = CoreLocalClosure { EmptyDispatchClosure }

    private val scope by lazy {
        CoreDispatchScope(
            closure = closure,
            dispatchFunction = dispatchFunction,
            getState = { }
        )
    }

    @Test
    fun launchForegroundShouldRegisterJobInLocalJobRegistry() {
        closure = CoreLocalClosure { registry + dispatchCoroutineScope }
        val job = scope.launchForeground { }
        registry.consumeOrNull() shouldBe job
    }

    @Test
    fun launchForegroundShouldLaunchJobInLocalDispatchCoroutineScope() {
        closure = CoreLocalClosure { registry + dispatchCoroutineScope }
        val job = scope.launchForeground { }
        dispatchCoroutineScope.coroutineContext.job.children shouldContain job
    }

    @Test
    fun launchInForegroundShouldLaunchForegroundJob() = runTest {
        closure = CoreLocalClosure { registry + DispatchCoroutineScope(this) }
        val job = flowOf(1, 2, 3).launchInForeground(scope)
        registry.consumeOrNull() shouldBe job
    }

    @Test
    fun launchInForegroundShouldCollectFlowUsingLaunchForeground() = runTest {
        closure = CoreLocalClosure { registry + DispatchCoroutineScope(this) }
        val elements = mutableListOf<Int>()
        val job = flowOf(1, 2, 3)
            .onEach(elements::add)
            .launchInForeground(scope)
        job.join()
        elements shouldBe listOf(1, 2, 3)
    }

    @Test
    fun dispatchJobShouldNotFailOnLaunchForeground() {
        closure = CoreLocalClosure { dispatchCoroutineScope }
        dispatchFunction = { scope.launchForeground { } }
        shouldNotThrowAny { scope.dispatchJob(TestJobAction) }
    }

    @Test
    fun dispatchJobShouldThrowIllegalArgumentExceptionWhenForegroundJobNotRegistered() {
        closure = CoreLocalClosure { dispatchCoroutineScope }
        dispatchFunction = { }
        shouldThrow<IllegalArgumentException> { scope.dispatchJob(TestJobAction) }
    }

    @Test
    fun dispatchJobShouldReturnRegisteredJob() {
        closure = CoreLocalClosure { dispatchCoroutineScope }
        var registeredJob: Job? = null
        dispatchFunction = { registeredJob = scope.launchForeground { } }
        scope.dispatchJob(TestJobAction) shouldBe registeredJob
    }

    @Test
    fun dispatchJobInShouldNotFailOnLaunchForeground() {
        dispatchFunction = { scope.launchForeground { } }
        shouldNotThrowAny { scope.dispatchJobIn(TestJobAction, dispatchCoroutineScope) }
    }

    @Test
    fun dispatchJobInShouldThrowIllegalArgumentExceptionWhenForegroundJobNotRegistered() {
        dispatchFunction = { }
        shouldThrow<IllegalArgumentException> { scope.dispatchJobIn(TestJobAction, dispatchCoroutineScope) }
    }

    @Test
    fun dispatchJobInShouldProvideLocalDispatchCoroutineScope() {
        dispatchFunction = {
            scope.launchForeground {  }
            scope.localClosure.find(DispatchCoroutineScope) shouldNotBe null
        }
        scope.dispatchJobIn(TestJobAction, TestScope())
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
        scope.joinDispatchJob(TestJobAction)
        joined shouldBe true
    }
}