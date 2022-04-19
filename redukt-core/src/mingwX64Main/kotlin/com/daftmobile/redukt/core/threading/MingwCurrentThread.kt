package com.daftmobile.redukt.core.threading

import platform.windows.GetCurrentThreadId

internal actual fun Thread.Companion.current(): Thread = Thread("#${GetCurrentThreadId()}")