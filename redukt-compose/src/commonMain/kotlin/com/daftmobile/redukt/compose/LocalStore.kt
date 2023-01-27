package com.daftmobile.redukt.compose


import androidx.compose.runtime.*
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.coroutines.ForegroundJobAction
import com.daftmobile.redukt.core.coroutines.dispatchJob
import com.daftmobile.redukt.core.coroutines.dispatchJobIn
import com.daftmobile.redukt.core.coroutines.joinDispatchJob
import com.daftmobile.redukt.core.store.Selector
import com.daftmobile.redukt.core.store.SelectorFunction
import com.daftmobile.redukt.core.store.Store
import com.daftmobile.redukt.core.store.select
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * Creates static [CompositionLocal] for a [Store] with a given state [T].
 */
public fun <T> localStoreOf(
    defaultFactory: () -> Store<T> = { null!! }
): ProvidableCompositionLocal<Store<T>> = staticCompositionLocalOf(defaultFactory = defaultFactory)

/**
 * Returns a dispatch function for a composition local store.
 */
public inline val <AppState> CompositionLocal<Store<AppState>>.dispatch: DispatchFunction
    @Composable
    get() {
        val store = current
        return remember { { store.dispatch(it) } }
    }

/**
 * Returns a dispatchJob function for a composition local store.
 */
public inline val <AppState> CompositionLocal<Store<AppState>>.dispatchJob: (ForegroundJobAction) -> Job
    @Composable
    get() {
        val store = current
        return remember { { store.dispatchJob(it) } }
    }

/**
 * Returns a [dispatchJobIn] function for a composition local store.
 */
public inline val <AppState> CompositionLocal<Store<AppState>>.dispatchJobIn: (ForegroundJobAction, CoroutineScope) -> Unit
    @Composable
    get() {
        val store = current
        return remember { { action, scope -> store.dispatchJobIn(action, scope) } }
    }


/**
 * Returns a [joinDispatchJob] function for a composition local store.
 */
public inline val <AppState> CompositionLocal<Store<AppState>>.joinDispatchJob: suspend (ForegroundJobAction) -> Unit
    @Composable
    get() {
        val store = current
        return remember { { store.joinDispatchJob(it) } }
    }



/**
 * Returns a [dispatchJobIn] function for a composition local store. It provides a [CoroutineScope] from [rememberCoroutineScope].
 * It results in an automatic cancellation of any foreground coroutine triggered by this function at a composable disposal.
 */
public inline val <AppState> CompositionLocal<Store<AppState>>.dispatchJobInHere: (ForegroundJobAction) -> Unit
    @Composable
    get() {
        val store = current
        val scope = rememberCoroutineScope()
        return remember { { store.dispatchJobIn(it, scope) } }
    }

/**
 * Selects part of a state using given [selector] as a Compose [State].
 */
@Composable
public fun <AppState, Selected> Store<AppState>.selectAsState(
    selector: Selector<AppState, Selected>
): State<Selected> = remember(selector) { select(selector) }.collectAsState()

/**
 * Selects part of a state as a Compose [State] from composition local store using [selectorFunction].
 */
@Composable
public fun <AppState, Selected> Store<AppState>.selectAsState(
    selectorFunction: SelectorFunction<AppState, Selected>,
): State<Selected> = remember(selectorFunction) { select(selectorFunction) }.collectAsState()

/**
 * Selects part of a state as a Compose [State] from composition local store using [selector].
 */
@Composable
public fun <AppState, Selected> CompositionLocal<Store<AppState>>.selectAsState(
    selector: Selector<AppState, Selected>
): State<Selected> = current.selectAsState(selector)
