package com.daftmobile.redukt.core.threading

private val MainThread = Thread("JsMain")

actual fun Thread.Companion.current(): Thread = MainThread