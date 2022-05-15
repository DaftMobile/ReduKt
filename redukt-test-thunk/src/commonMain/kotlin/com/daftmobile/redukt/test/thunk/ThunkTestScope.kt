package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.test.assertions.ActionsAssertScope

interface ThunkTesterScope<T> : ActionsAssertScope {

    val returnProcessed: Boolean

    fun getReturn(): T
}

internal class DefaultThunkTesterScope<T>(
    private val result: Result<T>,
    scope: ActionsAssertScope
) : ThunkTesterScope<T>, ActionsAssertScope by scope {
    override var returnProcessed: Boolean = false
    private set

    override fun getReturn(): T {
        returnProcessed = true
        return result.getOrThrow()
    }
}