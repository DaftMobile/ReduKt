package com.daftmobile.redukt.core

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

/**
 * Marker interface for every
 * [Redux action](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#actions).
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("ReduKtAction", exact = true)
public interface Action

/**
 * Type alias for
 * [Redux dispatch function](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#dispatch).
 * In contrast to Redux dispatch, it returns no value.
 */
public typealias DispatchFunction = (action: Action) -> Unit
