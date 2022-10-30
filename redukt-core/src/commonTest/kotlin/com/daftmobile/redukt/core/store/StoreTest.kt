package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.middleware.Middleware

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