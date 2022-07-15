package com.daftmobile.redukt.core.threading

import platform.Foundation.NSThread

public actual fun KtThread.Companion.current(): KtThread = KtThread(NSThread.currentThread.name)