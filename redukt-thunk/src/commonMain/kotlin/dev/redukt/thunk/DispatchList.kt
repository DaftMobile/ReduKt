package dev.redukt.thunk

import dev.redukt.core.Action
import dev.redukt.core.coroutines.ForegroundJobAction
import dev.redukt.core.coroutines.dispatchJobIn
import dev.redukt.core.coroutines.joinDispatchJob
import kotlinx.coroutines.coroutineScope

public data class DispatchList(val actions: List<Action>): Thunk<Unit>({ actions.forEach(::dispatch) })

public operator fun Action.plus(other: Action): DispatchList = DispatchList(unwrapped() + other.unwrapped())

public data class DispatchJobSupportList(val actions: List<Action>, val concurrent: Boolean): CoThunk<Unit>({
    if (concurrent) coroutineScope {
        actions.forEach {
            if (it is ForegroundJobAction) dispatchJobIn(it, this)
            else dispatch(it)
        }
    } else {
        actions.forEach {
            if (it is ForegroundJobAction) joinDispatchJob(it)
            else dispatch(it)
        }
    }
})

public data class DispatchJobSupport(val concurrent: Boolean = false)

public infix fun DispatchList.with(support: DispatchJobSupport): DispatchJobSupportList {
    return DispatchJobSupportList(actions, support.concurrent)
}

private fun Action.unwrapped(): List<Action> = if (this is DispatchList) actions else listOf(this)
