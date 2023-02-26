# ReduKt Core

## Before you start

Before diving into this guide, you should be familiar
with [Redux terminology](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#redux-terminology).
It's worth to notice that some elements are missing/changed/added in comparison to JS Redux.

## Guide

1. [Create a store](#create-a-store)
2. [Define actions](#define-actions)
3. [Define reducers](#define-reducers)
4. [Select and subscribe the state](#select-and-subscribe-the-state)
5. [Define middlewares](#define-middlewares)
6. [Dispatch closure basics](#dispatch-closure-basics)
7. [Working with coroutines](#working-with-coroutines)
8. [Thread safety](#thread-safety)

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

### Select and subscribe the state

There is no `subscribe` method in ReduKt Store. Instead, store provides `state: StateFlow<AppState>` field.
To receive state updates you have to collect the flow:

```kotlin
store.state.collect { /* process state updates here */ }

// or more `subscribe-like` approach
store.state
    .onEach { /* process state updates here */ }
    .launchIn(scope)
```

To observe part of a state you have to use `select`.

```kotlin
val totalProductsPrice = store.select { it.products.sumBy(Product::price) }
totalProductsPrice.value // returns current state of total products price
totalProductsPrice.collect {
    /* this block is called only if total products price changes */
}
```

StateFlow returned by `select` is lazy by default and has the following properties:

* Selector function is not called before you access or collect selected state.
* Selector function is not called if state is not changed (by default it's compared using equals).
* If selector function returns the same value as previously (by default it's compared using equals), selected state is
  not emitted again (just like every StateFlow).

To optimize state selection you can use `select` with `Selector` param like this:

```kotlin
val totalProductsPriceSelector = Selector(
    stateEquality = { old, new -> old.products == new.products },
    selector = { it.products.sumBy(Product::price) }
)
// selected state is recalculated only if products list is changed
val totalProductsPrice = store.select(totalProductsPriceSelector)

totalProductsPrice.collect { }
```

It's also possible to change selected state equality function with `Selector`, but generally it should not be
necessary.

### Define middlewares

Middlewares in ReduKt differ a little from JS Redux. The key differences are:

* There is only 1 nested lambda instead of 2.
* `getState`, `dispatch` and `next` are available from middleware lambda receiver -  `MiddlewareScope`.
* `getState` function is replaced by `currentState` property.
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

There is also `consumingDispatch` helper that can be used like this:

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

### Dispatch closure basics

Essentially, dispatch closure is an immutable map of objects (elements) that is injected into the store, and it is available from
every middleware. It's primarily used to extend store's functionality.

Dispatch closure elements are associated with special keys. By convention, those keys are companion objects of closure
element classes.

It might seem a little confusing, but it should be clear with this example:

```kotlin
fun fooMiddleware() = middleware<AppState> { action ->
    // to get an instance of DispatchCoroutineScope you have to pass its companion object as a key to closure
    val scope = closure[DispatchCoroutineScope]
}
```



Dispatch closure might contain only one `DispatchCoroutineScope`, because map must contain only one value for a given
key. This applies for all closure elements introduced by this library. In other words, dispatch closure must contain only one object of given type.

Often closure elements come with handy extensions to access them. In case of `DispatchCoroutineScope` we can access it
like this:

```kotlin
fun fooMiddleware() = middleware<AppState> { action ->
    coroutineScope // equivalent of closure[DispatchCoroutineScope]
}
```

Closure has to be defined at store creation and remains immutable. Despite it being immutable, its
elements themselves might mutate.

```kotlin
val store = buildStore {
    // ...
    closure {
        +DispatchCoroutineScope(MainScope())
    }
}
```

As a dependency injection tool, dispatch closure might seem a little limited, because effectively it handles only
singletons.
Also, adding completely new types to it comes with a little boilerplate. That is because, dispatch closure is not
designed to be
a dependency injection tool. It is used to extend store's functionality.

Despite dispatch closure not being a perfect DI tool, we can integrate a real DI into the store using this mechanism.

In example below our DI tool of choice is [Kodein](https://github.com/kosi-libs/Kodein). In a first place we have to
implement a `DispatchClosure.Element` like this:

```kotlin
class KodeinDI(di: DI) : DI by di, DispatchClosure.Element {

    override val key: Key get() = Key

    companion object Key : DispatchClosure.Key<KodeinDI>
}
```

Now we have to add it into the store:

```kotlin
val store = buildStore {
    //...
    val di = DI {
        bindSingleton { HttpClient() }
        // ...
    }
    closure {
        +DispatchCoroutineScope(MainScope())
        +KodeinDI(di)
    }
}
```

Now KodeinDI is accessible from any middleware:

```kotlin
fun fooMiddleware() = middleware { action ->
    val di = closure[KodeinDI]
}
```

We can add an extension to make it easier to access DI:

```kotlin
val DispatchScope<*>.di: DI get() = closure[KodeinDI]
```

Final result:

```kotlin
fun fooMiddleware() = middleware<AppState> { action ->
    val httpClient: HttpClient by di.instance()
    // ...
}
```

### Working with coroutines

In ReduKt there is a concept of *foreground job*. It is a **single** coroutine that is logically associated with given
action.

Let's consider this kind of action:

```kotlin
data class FetchBook(val id: String) : Action
```

Here we expect that this action is going to trigger an asynchronous call to get the book. A coroutine that host this
call
is a foreground job, because it is logically associated with given action.

However, ReduKt store can't really figure out which action has associated coroutine, so action has to be marked:

```kotlin
data class FetchBook(val id: String) : ForegroundJobAction
```

Now we have to ensure that there is a middleware that launches a coroutine:

```kotlin
fun booksMiddleware(client: HttpClient) = consumingMiddleware<AppState, FetchBook> { action ->
    launchForeground {
        val response = client.get("$API_URL/book/${action.id}")
        // process the response
    }
}
```

`launchForeground` launches a coroutine in the `DispatchCoroutineScope` that is a `CoroutineScope` associated
with a store. By default, `DispatchCoroutineScope` is a `MainScope`, therefore foreground
coroutines work on the main thread in this case.

Now, what we can do with it?

In the most straightforward example, you can use `store.dispatchJob` to dispatch a `FetchBook` action. This function
returns a `Job`, so you are able to control the associated coroutine.

```kotlin
val job = store.dispatchJob(FetchBook("1"))
// ...
job.cancel()
```

You can change a `CoroutineScope` for the associated coroutine with `store.dispatchJobIn`:

```kotlin
val scope = MainScope()

store.dispatchJobIn(FetchBook("1"), scope)

scope.cancel() // results in the associated coroutine being cancelled
```
Main purpose of `dispatchJobIn` is providing a `CoroutineScope` that has a shorter lifecycle than `DispatchCoroutineScope`. 
Using it to change a `CoroutineDispatcher` is technically correct, but also discouraged.

You can wait for the coroutine with `store.joinDispatchJob` like this:

```kotlin
fun initMiddleware() = middleware<AppState> { action ->
    if (action is InitAction) {
        launchForeground {
            joinDispatchJob(FetchUserData)
            val currentBookId = currentState.user.currentBookId
            joinDispatchJob(FetchBook(currentBookId))
        }
    }
    next(action)
}
```

A foreground job that is launched by the `joinDispatchJob` is cancelled along with parent coroutine that dispatched it.

### Thread safety

ReduKt store is designed to be accessed from a single thread. It should be the same thread as the one bound
to `DispatchCoroutineScope`. By default, ReduKt store contains an instance of `MainScope` so you have to access the
store from the main thread.

Despite all of this, ReduKt store is partially thread-safe. You can access or collect its state from different threads.
That's because the state is stored within a `StateFlow`. Dispatch closure is effectively an immutable collection of
objects, so it is also thread-safe. However, closure elements might mutate, and it might not be safe to interact with
them from another thread.

The only thing that is definitely **not thread-safe** is `dispatch`.

If you want to make sure that you are calling `dispatch` from single thread, you can use `threadGuardMiddleware` like
this:

```kotlin
val store = buildStore {
    middlewares {
        +threadGuradMiddleware // should be first in the pipeline
        // ...
    }
    // ...
}
```

This middleware remembers the thread used to create a store and verifies if `dispatch` is called with this thread.
If you use different thread, `threadGuardMiddleware` throws an exception.

If you really want to call the `dispatch` from another thread, you can use `store.synchronized {}` like this:

```kotlin
store.synchronized { dispatch(action) }
```

`Store.synchronized` launches given block in a coroutine within `DispatchCoroutineScope`.
