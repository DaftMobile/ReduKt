package com.daftmobile.redukt.core.threading

import platform.Foundation.NSThread

actual fun KtThread.Companion.current(): KtThread = KtThread(NSThread.currentThread.name)