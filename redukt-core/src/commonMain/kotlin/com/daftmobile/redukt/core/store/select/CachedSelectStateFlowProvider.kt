package com.daftmobile.redukt.core.store.select

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration

@Suppress("UNCHECKED_CAST")
internal class CachedSelectStateFlowProvider(
    private val newStateFlowProvider: SelectStateFlowProvider,
    private val unusedFlowTimeout: Duration,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : SelectStateFlowProvider {

    private val cache = SynchronizedWrapper(mutableMapOf<Selector<*, *>, StateFlow<*>>())

    override fun <State, Selected> provide(
        state: StateFlow<State>,
        selector: Selector<State, Selected>
    ): StateFlow<Selected> = cache.use {
        val cachedFlow = it[selector]
        if (cachedFlow != null) return@use cachedFlow as StateFlow<Selected>
        val newFlow = newStateFlowProvider.provide(state, selector)
        it[selector] = newFlow
        scheduleCleanUp(selector, newFlow)
        return@use newFlow
    }

    @OptIn(FlowPreview::class)
    private fun scheduleCleanUp(selector: Selector<*, *>, stateFlow: StateFlow<*>) {
        if (stateFlow !is SelectStateFlow<*, *>) return
        scope.launch {
            stateFlow
                .subscribers
                .debounce(unusedFlowTimeout)
                .collect { subscribers ->
                    if (subscribers == 0) {
                        cache.use { it.remove(selector) }
                        cancel()
                    }
                }
        }
    }
}

private class SynchronizedWrapper<T>(val ref: T): SynchronizedObject() {

    inline fun <R> use(block: (T) -> R): R = synchronized(this) { block(ref) }
}
