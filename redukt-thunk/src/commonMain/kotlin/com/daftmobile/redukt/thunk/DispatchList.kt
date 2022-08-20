package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.SuspendAction
import com.daftmobile.redukt.core.coroutines.asyncDispatchIn
import com.daftmobile.redukt.core.coroutines.awaitDispatch
import kotlinx.coroutines.coroutineScope

public data class DispatchList(val actions: List<Action>): Thunk<Unit>({ actions.forEach(::dispatch) })

public operator fun Action.plus(other: Action): DispatchList = DispatchList(unwrapped() + other.unwrapped())

public data class SuspendingDispatchList(val actions: List<Action>, val concurrent: Boolean): CoThunk<Unit>({
    if (concurrent) coroutineScope {
        actions.forEach {
            if (it is SuspendAction) asyncDispatchIn(it, this)
            else dispatch(it)
        }
    } else {
        actions.forEach {
            if (it is SuspendAction) awaitDispatch(it)
            else dispatch(it)
        }
    }
})

public fun suspensionSupport(dispatchList: DispatchList, concurrent: Boolean = false): SuspendingDispatchList {
    return SuspendingDispatchList(dispatchList.actions, concurrent)
}

private fun Action.unwrapped(): List<Action> = if (this is DispatchList) actions else listOf(this)
