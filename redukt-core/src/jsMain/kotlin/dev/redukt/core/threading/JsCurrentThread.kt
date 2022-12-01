package dev.redukt.core.threading

private val JsMainKtThread = KtThread("JsMain")

/**
 * [KtThread] representation of JS main thread.
 */
public val KtThread.Companion.JsMainThread: KtThread get() = JsMainKtThread

/**
 * Returns constant [JsMainThread].
 */
public actual fun KtThread.Companion.current(): KtThread = JsMainThread