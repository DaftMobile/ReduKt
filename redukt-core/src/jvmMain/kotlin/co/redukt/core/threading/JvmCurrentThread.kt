package co.redukt.core.threading

/**
 * Returns [KtThread] depending on [Thread.currentThread](https://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#currentThread()) from Java Standard Library.
 */
public actual fun KtThread.Companion.current(): KtThread = KtThread(Thread.currentThread().name)