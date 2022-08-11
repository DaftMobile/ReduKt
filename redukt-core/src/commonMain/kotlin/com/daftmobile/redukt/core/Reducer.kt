package com.daftmobile.redukt.core

public typealias Reducer<T> = (T, Action) -> T

public fun <T> combineReducers(vararg reducers: Reducer<T>): Reducer<T> = { initialState, action ->
    reducers.fold(initialState) { prevState, reducer -> reducer(prevState, action) }
}