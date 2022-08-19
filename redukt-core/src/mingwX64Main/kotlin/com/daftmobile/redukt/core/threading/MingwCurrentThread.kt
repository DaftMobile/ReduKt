package com.daftmobile.redukt.core.threading

import platform.windows.GetCurrentThreadId

public actual fun KtThread.Companion.current(): KtThread = KtThread("#${GetCurrentThreadId()}")