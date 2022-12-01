package co.redukt.core.store

import co.redukt.core.ClosureElementA
import co.redukt.core.ClosureElementB
import co.redukt.core.ClosureElementC
import co.redukt.core.Reducer
import co.redukt.core.closure.DispatchClosure
import co.redukt.core.closure.LocalClosure
import co.redukt.core.coroutines.ForegroundJobRegistry
import co.redukt.core.middleware.Middleware

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
            initialClosure.find(LocalClosure)?.unaryPlus()
        }
    }
}