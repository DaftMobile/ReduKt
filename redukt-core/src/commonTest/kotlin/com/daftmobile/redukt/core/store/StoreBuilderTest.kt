package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.ClosureElementA
import com.daftmobile.redukt.core.ClosureElementB
import com.daftmobile.redukt.core.ClosureElementC
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer
import com.daftmobile.redukt.core.coroutines.ForegroundJobRegistry
import com.daftmobile.redukt.core.middleware.Middleware

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