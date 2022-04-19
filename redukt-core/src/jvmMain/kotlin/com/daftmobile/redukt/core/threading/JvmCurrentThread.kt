package com.daftmobile.redukt.core.threading

actual fun KtThread.Companion.current(): KtThread = KtThread(Thread.currentThread().name)