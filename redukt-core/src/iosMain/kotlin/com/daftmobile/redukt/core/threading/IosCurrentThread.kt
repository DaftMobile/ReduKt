package com.daftmobile.redukt.core.threading

import platform.Foundation.NSThread

/**
 * Returns [KtThread] depending on [NSThread.currentThread](https://developer.apple.com/documentation/foundation/nsthread/1410679-currentthread).
 */
public actual fun KtThread.Companion.current(): KtThread = KtThread(NSThread.currentThread.name)