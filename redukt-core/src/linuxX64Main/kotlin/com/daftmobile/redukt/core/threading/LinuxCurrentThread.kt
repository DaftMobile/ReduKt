package com.daftmobile.redukt.core.threading

import platform.posix.pthread_self

/**
 * Returns [KtThread] depending on [pthread_self](https://learn.microsoft.com/en-us/windows/win32/api/processthreadsapi/nf-processthreadsapi-getcurrentthreadid) from POSIX.
 */
public actual fun KtThread.Companion.current(): KtThread = KtThread("#${pthread_self()}")