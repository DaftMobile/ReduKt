package com.daftmobile.redukt.core.threading

private val MainThread = KtThread("JsMain")

actual fun KtThread.Companion.current(): KtThread = MainThread