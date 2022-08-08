package com.daftmobile.redukt.core

public fun <T> combineReducers(vararg reducers: Reducer<T>): Reducer<T> = { initialState, action ->
    reducers.fold(initialState) { prevState, reducer -> reducer(prevState, action) }
}