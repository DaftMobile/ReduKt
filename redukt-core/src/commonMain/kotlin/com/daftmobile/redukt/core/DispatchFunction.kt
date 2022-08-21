package com.daftmobile.redukt.core

public interface Action

public interface JobAction : Action

public typealias DispatchFunction = (action: Action) -> Unit
