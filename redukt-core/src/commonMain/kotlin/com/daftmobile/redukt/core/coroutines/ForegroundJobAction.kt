package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.Action

/**
 * Marker interface for every [Action] that has a *foreground job* ([kotlinx.coroutines.Job]) logically associated with it.
 * This kind of action **must** result in a **single** coroutine being launched by a middleware using [launchForeground].
 * Handle to this coroutine is returned by [dispatchJob] or it might be simply joined using [joinDispatchJob].
 * CoroutineScope depends on method used to dispatch it.
 *
 * Foreground job concept was introduced to await or cancel any asynchronous operations associated with an action.
 */
public interface ForegroundJobAction : Action