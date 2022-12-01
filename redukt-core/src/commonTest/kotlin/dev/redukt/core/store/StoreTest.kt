package dev.redukt.core.store

import dev.redukt.core.Reducer
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.middleware.Middleware

class StoreTest : BaseStoreTest() {
    override fun provideStoreToTest(
        initialState: Int,
        reducer: Reducer<Int>,
        middlewares: List<Middleware<Int>>,
        initialClosure: DispatchClosure
    ): Store<Int> = Store(
        initialState,
        reducer,
        middlewares,
        initialClosure
    )
}