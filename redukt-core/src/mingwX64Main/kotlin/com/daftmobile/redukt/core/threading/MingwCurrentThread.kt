package com.daftmobile.redukt.core.threading

import platform.windows.GetCurrentThreadId

/**
 * Returns [KtThread] depending on [GetCurrentThreadId](https://learn.microsoft.com/en-us/windows/win32/api/processthreadsapi/nf-processthreadsapi-getcurrentthreadid) from Windows API.
 */
public actual fun KtThread.Companion.current(): KtThread = KtThread("#${GetCurrentThreadId()}")