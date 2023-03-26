package com.daftmobile.redukt.core.threading

import platform.Foundation.NSThread

/**
 * Returns [KtThread] depending on [NSThread.currentThread](https://developer.apple.com/documentation/foundation/nsthread/1410679-currentthread) from Apple Foundation Framework.
 */
public actual fun KtThread.Companion.current(): KtThread = NSThread
    .currentThread()
    .let(NSThread::identifier)
    .let(::KtThread)

private val NSThread.identifier: String?
    get() = when {
        isMainThread -> "main"
        !name.isNullOrBlank() -> name
        else -> description?.takeIf { it.isNotBlank() }
    }