package co.redukt.core.store

import co.redukt.core.Reducer
import co.redukt.core.closure.DispatchClosure
import co.redukt.core.middleware.Middleware

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