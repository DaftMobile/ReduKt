package com.daftmobile.redukt.core.threading

import platform.windows.GetCurrentThreadId

actual fun KtThread.Companion.current(): KtThread = KtThread("#${GetCurrentThreadId()}")