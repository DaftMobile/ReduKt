package com.daftmobile.redukt.core.threading

private val MainThread = KtThread("JsMain")

public actual fun KtThread.Companion.current(): KtThread = MainThread