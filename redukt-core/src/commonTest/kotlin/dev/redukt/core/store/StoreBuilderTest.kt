package dev.redukt.core.store

import dev.redukt.core.ClosureElementA
import dev.redukt.core.ClosureElementB
import dev.redukt.core.ClosureElementC
import dev.redukt.core.Reducer
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.LocalClosureContainer
import dev.redukt.core.coroutines.ForegroundJobRegistry
import dev.redukt.core.middleware.Middleware

class StoreBuilderTest : BaseStoreTest() {

    override fun provideStoreToTest(
        initialState: Int,
        reducer: Reducer<Int>,
        middlewares: List<Middleware<Int>>,
        initialClosure: DispatchClosure
    ): Store<Int> = buildStore {
        initialState reducedBy reducer
        middlewares {
            middlewares.getOrNull(0)?.unaryPlus()
            middlewares.getOrNull(1)?.unaryPlus()
        }
        closure {
            initialClosure.find(ClosureElementA)?.unaryPlus()
            initialClosure.find(ClosureElementB)?.unaryPlus()
            initialClosure.find(ClosureElementC)?.unaryPlus()
        }
        middlewares {
            middlewares.getOrNull(2)?.unaryPlus()
        }
        closure {
            initialClosure.find(ForegroundJobRegistry)?.unaryPlus()
            initialClosure.find(LocalClosureContainer)?.unaryPlus()
        }
    }
}