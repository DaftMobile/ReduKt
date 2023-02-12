package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.ClosureElementA
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer
import com.daftmobile.redukt.core.coroutines.EmptyForegroundJobRegistry
import com.daftmobile.redukt.core.coroutines.ForegroundJobRegistry
import com.daftmobile.redukt.core.coroutines.SingleForegroundJobRegistry
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.coroutines.DisabledLocalClosureContainer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

abstract class BaseStoreTest {

    private val mockReducer = MockReducer<Int> { _, _ -> 2 }
    private val mockMiddleware1 = MockMiddleware<Int>()
    private val mockMiddleware2 = MockMiddleware<Int>()
    private val mockMiddleware3 = MockMiddleware<Int>()

    private val middlewares get() = listOf(mockMiddleware1, mockMiddleware2, mockMiddleware3)

    private val initialState = 1
    private var initialClosure: DispatchClosure = EmptyDispatchClosure

    abstract fun provideStoreToTest(
        initialState: Int,
        reducer: Reducer<Int>,
        middlewares: List<Middleware<Int>>,
        initialClosure: DispatchClosure
    ): Store<Int>

    private var useStoreWithMiddlewares = true
    private val store by lazy {
        if (useStoreWithMiddlewares) {
            provideStoreToTest(initialState, mockReducer::call, middlewares.map { it::call }, initialClosure)
        } else {
            provideStoreToTest(initialState, mockReducer::call, emptyList(),  initialClosure)
        }
    }

    @Test
    fun shouldHaveProperInitialState() {
        store.state.value shouldBe 1
    }

    @Test
    fun shouldHaveEmptyForegroundRegistryInClosure() {
        store.closure.find(ForegroundJobRegistry).shouldBeInstanceOf<EmptyForegroundJobRegistry>()
    }

    @Test
    fun shouldAddInitialClosureToInternalClosure() {
        val testClosure = ClosureElementA()
        initialClosure = testClosure
        store.closure.find(ClosureElementA) shouldBe testClosure
    }

    @Test
    fun shouldNotOverwriteInternalClosureWithoutClientIntention() {
        initialClosure = ClosureElementA()
        storeShouldContainOriginalClosure()
    }

    @Test
    fun shouldOverwriteForegroundJobRegistryWhenInitialClosureContainsCorrectReplacement() {
        val replacedRegistry = SingleForegroundJobRegistry()
        initialClosure = replacedRegistry
        store.closure.find(ForegroundJobRegistry) shouldBe replacedRegistry
        store.closure.find(LocalClosureContainer).shouldNotBeNull()
    }

    @Test
    fun shouldOverwriteLocalClosureWhenInitialClosureContainsCorrectReplacement() {
        val replacedLocal = DisabledLocalClosureContainer(EmptyDispatchClosure)
        initialClosure = replacedLocal
        store.closure.find(LocalClosureContainer) shouldBe replacedLocal
        store.closure.find(ForegroundJobRegistry).shouldBeInstanceOf<EmptyForegroundJobRegistry>()
    }

    @Test
    fun shouldThrowUninitializedDispatchExceptionWhenActionDispatchedInMiddlewareCreator() {
        mockMiddleware1.onCreate = { dispatch(UnknownAction) }
        shouldThrow<UninitializedDispatchException> { store }
    }

    @Test
    fun shouldThrowUnsafeDispatchExceptionWhenDispatchInReducer() {
        useStoreWithMiddlewares = false
        mockReducer.onReducerCall = { _, _ -> store.dispatch(KnownAction.B); 1 }
        shouldThrow<UnsafeDispatchException> {
            store.dispatch(KnownAction.A)
        }
    }

    @Test
    fun shouldCallReducerWithProperArguments() {
        useStoreWithMiddlewares = false
        store.dispatch(KnownAction.A)
        mockReducer.receivedAction shouldBe KnownAction.A
        mockReducer.receivedState shouldBe 1
    }

    @Test
    fun shouldCallReducerWhenNoMiddlewares() {
        useStoreWithMiddlewares = false
        store.dispatch(KnownAction.A)
        mockReducer.wasCalled shouldBe true
    }

    @Test
    fun shouldUpdateStateWithReducerReturnedValue() {
        useStoreWithMiddlewares = false
        store.dispatch(KnownAction.A)
        store.state.value shouldBe 2
    }

    @Test
    fun shouldReturnUpdatedStateAsCurrentState() {
        useStoreWithMiddlewares = false
        store.dispatch(KnownAction.A)
        store.currentState shouldBe 2
    }

    @Test
    fun shouldCallReducerAfterLastMiddlewareNext() {
        middlewares.forEach { m -> m.dispatchFunction = { next(it) } }
        store.dispatch(KnownAction.A)
        mockReducer.wasCalled shouldBe true
    }

    @Test
    fun shouldNotCallReducerWhenLastMiddlewareNotCallingNext() {
        middlewares.forEach { m -> m.dispatchFunction = { } }
        store.dispatch(KnownAction.A)
        mockReducer.wasCalled shouldBe false
    }

    @Test
    fun shouldCreateMiddlewareChainWithCorrectOrder() {
        val checkpoints = mutableListOf<Int>()
        middlewares.forEachIndexed { index, m -> m.dispatchFunction = { checkpoints.add(index); next(it) } }
        store.dispatch(KnownAction.A)
        checkpoints shouldBe listOf(0, 1, 2)
    }

    @Test
    fun shouldCreateMiddlewareChainThatCanBeStopped() {
        val checkpoints = mutableListOf<Int>()
        mockMiddleware1.dispatchFunction = { checkpoints.add(0); next(it) }
        mockMiddleware2.dispatchFunction = { checkpoints.add(1) }
        mockMiddleware3.dispatchFunction = { checkpoints.add(2); next(it) }
        store.dispatch(KnownAction.A)
        checkpoints shouldBe listOf(0, 1)
    }

    @Test
    fun shouldCreateMiddlewareChainThatCanChangePassedAction() {
        mockMiddleware1.dispatchFunction = { next(it) }
        mockMiddleware2.dispatchFunction = { next(KnownAction.B) }
        mockMiddleware3.dispatchFunction = { next(it) }
        store.dispatch(KnownAction.A)
        mockMiddleware3.lastReceivedAction shouldBe KnownAction.B
    }

    @Test
    fun shouldDeliverUpdatedStateToMiddlewares() {
        middlewares.forEach { m -> m.dispatchFunction = { next(it) } }
        mockReducer.onReducerCall = { _, action ->
            when (action) {
                KnownAction.B -> 4
                else -> 2
            }
        }
        store.dispatch(KnownAction.B)
        store.dispatch(KnownAction.A)
        middlewares.forEach {
            it.lastReceivedState shouldBe 4
        }
    }

    @Test
    fun shouldDeliverDispatchThatAllowsMiddlewaresToCallDispatchProperly() {
        mockMiddleware1.dispatchFunction = { next(it) }
        mockMiddleware2.dispatchFunction = {
            when (it) {
                KnownAction.A -> dispatch(KnownAction.B)
                else -> next(it)
            }
        }
        mockMiddleware3.dispatchFunction = { next(it) }
        store.dispatch(KnownAction.A)
        middlewares.shouldForAll { it.lastReceivedAction shouldBe KnownAction.B }
    }

    private fun storeShouldContainOriginalClosure() {
        store.closure.find(ForegroundJobRegistry).shouldBeInstanceOf<EmptyForegroundJobRegistry>()
        store.closure.find(LocalClosureContainer).shouldNotBeNull()
    }
}

