# ReduKt Core

[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](http://kotlinlang.org)

## Before you start

Before diving into this guide, you should be familiar
with [Redux terminology](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#redux-terminology).
It's worth to notice that some elements are missing/changed/added in comparison to JS Redux.

## Guide

1. [Create a store](#create-a-store)
2. [Define actions](#define-actions)
3. [Define reducers](#define-reducers)
4. [Define middlewares](#define-middlewares)
5. [Working with coroutines](#working-with-coroutines)
6. [Dispatch closure basics](#dispatch-closure-basics)
7. [Thread safety tools](#thread-safety-tools)

### Create a store

To create a store you should use DSL provided by `buildStore` function.

```kotlin
data class AppState(
    val posts: List<Post> = emptyList(),
    val user: User? = null
)

fun appReducer(state: AppState, action: Action) = AppState(
    posts = postReducer(state.posts, action),
    user = userReducer(state.user, action)
)

val store: Store<AppState> = buildStore {

    AppState() reducedBy ::appReducer // initial state and root reducer

    middlewares { // declares middlewares pipeline in given order
        +threadGuradMiddleware // first in the pipeline
        if (IS_DEBUG) +debugMiddleware() // second in the pipeline, but only in debug version of the app
    }
}
```

`middlewares` block might be divided into multiple blocks like this.

```kotlin
middlewares { // declares middlewares pipeline in given order
    +threadGuradMiddleware // first in the pipeline
}
// ...
middlewares {
    if (IS_DEBUG) +debugMiddleware() // second in the pipeline, but only in debug version of the app
}
```

### Define actions

Action must be marked with `Action` interface.

```kotlin
object InitAction : Action
```

Actions can be grouped using sealed classes/interfaces.

```kotlin
sealed interface UserAction : Action {
    data class LoggedIn(val user: User) : UserAction
    object Logout : UserAction
}
```

### Define reducers

Reducers might be defined simply as functions:

```kotlin
fun userReducer(user: User, action: Action): User = when (action) {
    is UserAction.LoggedIn -> TOOD()
    else -> user
}
```

Also, you can define reducers as properties with lambdas:

```kotlin
val userReducer: Reducer<User> = { user, action ->
    when (action) {
        is UserAction.LoggedIn -> TOOD()
        else -> user
    }
}
```

You can combine multiple reducers of the same type into single one with `combineReducers` function.

```kotlin
val userReducer1: Reducer<User> = TODO()

fun userReducer2(user: User, action: Action): User = TODO()

val combinedUserReducer: Reducer<User> = combineReducers(userReducer1, ::userReducer2)
```

Combined reducers apply changes to state in a given order.
> **Warning**: JS Redux also has `combineReducers` function, but it works differently!

### Define middlewares

Middlewares in ReduKt differ a little from JS Redux. The key differences are:

* There is only 1 nested lambda instead of 2.
* `getState`, `dispatch` and `next` are available from middleware lambda receiver -  `MiddlewareScope`
* `MiddlewareScope` also provides `closure`. Read more about *DispatchClosure* [here](#dispatch-closure-basics).

The most straightforward way to define a middleware:

```kotlin
fun debugMiddleware(): Middleware<AppState> = {
    var actionsCounter = 0 // this variable has middleware lifetime
    { action ->
        println("[${actionsCounter++}] $action")
        next(action)
        println("New state: $currentState")
    }
}
```

If you don't need to define variables like `actionsCounter`, you can use `middleware` helper function like this:

```kotlin
fun debugMiddleware() = middleware<AppState> { action ->
    println(action)
    next(action)
    println("New state: $currentState")
}
```

If you always want to call `next` and don't need fine control over it, you can use `translucentMiddleware`:

```kotlin
fun debugMiddleware() = translucentMiddleware<AppState> { action ->
    println(action)
    // next is always called after this block
}
```

There is also `translucentDispatch` helper that can be used like this:

```kotlin
fun debugMiddleware(): Middleware<AppState> = {
    var actionCounter = 0
    translucentDispatch { action ->
        println("[${actionCounter++}] $action")
        // next is always called after this block
    }
}
```

If you want to *consume* certain type of actions by your middleware and pass others to the next one, you can
use `consumingMiddleware`:

```kotlin
fun fooMiddleware() = consumingMiddleware<AppState, FooAction> { action ->
    // action is always a FooAction and there is no need to cast
    // next is called only if action is not a FooAction
}
```

There is also `consumeDispatch` helper that can be used like this:

```kotlin
fun fooMiddleware(): Middleware<AppState> = {
    consumingDispatch<FooAction> { action ->
        // action is always a FooAction and there is no need to cast
        // next is called only if action is not a FooAction
    }
}
```

Let's go back to the approach from the first middleware. It has two problems if we change it a little:

```kotlin
fun restorePreviousValue(): Int = TODO()

fun counterMiddleware(): Middleware<AppState> = {
    var i = restorePreviousValue() // semicolon is required here to compile
    { action -> // label must be defined manually here
        if (action is ResetCounter) {
            i = 0
            return next(action) // early exit requires a label to lambda
        }
        i++
        next(action)
    }
}
```

Normally, to fix it we would have to add a custom label and a semicolon. However, this semicolon is a little confusing
and naming labels is quite annoying.

Luckily, there is a simple solution to this problem. We can use `dispatchFunction` helper:

```kotlin
fun restorePreviousValue(): Int = TODO()

fun counterMiddleware(): Middleware<AppState> = {
   var i = restorePreviousValue() // semicolon is NOT required here to compile
   dispatchFunction { action ->
      if (action is ResetCounter) {
         i = 0
         return@dispatchFunction next(action) // default label can be referred here
      }
      i++
      next(action)
   }
}
```

### Working with coroutines

### Dispatch closure basics

### Thread safety tools

