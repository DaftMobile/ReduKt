package com.daftmobile.redukt.core.threading

import platform.windows.GetCurrentThreadId

actual fun Thread.Companion.current(): Thread = Thread("#${GetCurrentThreadId()}")