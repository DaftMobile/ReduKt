package com.daftmobile.redukt.core.threading

actual fun Thread.Companion.current(): Thread = Thread(java.lang.Thread.currentThread().name)