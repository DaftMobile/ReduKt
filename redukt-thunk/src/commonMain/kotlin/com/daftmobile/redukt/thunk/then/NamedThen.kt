package com.daftmobile.redukt.thunk.then

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.thunk.executeWith

open class NamedThen<T>(val then: Then<T>) : Then<T>() {

    final override val actions: List<Action> get() = then.actions

    final override suspend fun DispatchScope<Nothing>.execute(): T = then.executeWith(this)

    override fun toString(): String = super.toString() + " " + then.toString()
}