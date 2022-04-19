package com.daftmobile.redukt.core.threading

import platform.Foundation.NSThread

internal actual fun Thread.Companion.current(): Thread = Thread(NSThread.currentThread.name)