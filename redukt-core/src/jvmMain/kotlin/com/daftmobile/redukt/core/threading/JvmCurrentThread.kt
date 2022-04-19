package com.daftmobile.redukt.core.threading

internal actual fun Thread.Companion.current(): Thread = Thread(java.lang.Thread.currentThread().name)