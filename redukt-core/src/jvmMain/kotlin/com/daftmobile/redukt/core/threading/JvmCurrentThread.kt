package com.daftmobile.redukt.core.threading

public actual fun KtThread.Companion.current(): KtThread = KtThread(Thread.currentThread().name)