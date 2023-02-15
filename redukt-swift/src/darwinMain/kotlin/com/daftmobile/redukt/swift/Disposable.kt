package com.daftmobile.redukt.swift

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

/**
 * Represents a resource that should be disposed when no longer required.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("ReduKtDisposable", exact = true)
public fun interface Disposable {
    public fun dispose()
}