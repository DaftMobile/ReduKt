package com.daftmobile.redukt.kmm

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("ReduKtDisposable", exact = true)
public fun interface Disposable {
    public fun dispose()
}