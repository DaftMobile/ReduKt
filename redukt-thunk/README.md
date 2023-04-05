# ReduKt Thunk

Adaptation of [Redux thunk](https://redux.js.org/usage/writing-logic-thunks) in ReduKt.

### Basics

In ReduKt every thunk is an action that has some logic attached to it and evaluated by `thunkMiddleware`.
There are 2 types of thunks:

* Associated with regular function that is executed by `thunkMiddleware` in a blocking manner.

```kotlin
interface Thunk<State> : Action {
    fun DispatchScope<State>.execute()
}
```

* Associated with suspending function that is executed by `thunkMiddleware` in a foreground coroutine.

```kotlin
interface CoThunk<State> : ForegroundJobAction {
    suspend fun DispatchScope<State>.execute()
}
```

Every thunk has `DispatchScope` as `execute` method receiver. It provides `closure`, `dispatch` and `currentState`.

To create a thunk you can simply implement `Thunk` or `CoThunk` like this:

```kotlin
data class FetchBook(val id: String) : CoThunk<AppState> {
    suspend fun DispatchScope<AppState>.execute() {
        val client by koin.instance<HttpClient>()
        runCatching { client.get("/book/$id") }
            .onSuccess { dispatch(BookFetchSuccess(it)) }
            .onFailure { dispatch(BookFetchFailed(it)) }
    }
}
```
You can create thunks as separate types in a more compact way with `BaseThunk` and `BaseCoThunk`:

```kotlin
data class FetchBook(val id: String): BaseCoThunk<AppState>({
    val client by koin.instance<HttpClient>()
    runCatching { client.get("/book/$id") }
        .onSuccess { dispatch(BookFetchSuccess(it)) }
        .onFailure { dispatch(BookFetchFailed(it)) }
})
```

You can also instantiate it directly with `Thunk` or `CoThunk` functions:

```kotlin
fun fetchBook(id: String) = CoThunk<AppState> {
    val client by koin.instance<HttpClient>()
    runCatching { client.get("/book/$id") }
        .onSuccess { dispatch(BookFetchSuccess(it)) }
        .onFailure { dispatch(BookFetchFailed(it)) }
}
```

### DispatchList

ReduKt Thunk comes with one handy util called `DispatchList`. It allows you to wrap multiple actions into a single
action.
It can be created like this:

```kotlin
val action = DispatchList(listOf(ActionA, ActionB))
// or 
val action = ActionA + ActionB

store.dispatch(action) 
// it results in ActionA and ActionB being dispatched in given order.
```

`DispatchList` is a `Thunk` so it requires `thunkMiddleware`.

### JoiningCoroutinesDispatchList

If any action in `DispatchList` is a `ForegroundJobAction` you might want to wait for associated coroutine before
dispatching next action.
Example below shows how to do it:

```kotlin
val action = JoiningCoroutinesDispatchList(listOf(ActionA, JobActionB, JobActionC))
// or
val action = ActionA + JobActionB + JobActionC support JoiningCoroutines()

store.dispatch(action) 
// It results in ActionA being dispatched normally
// However, JobActionB and JobActionC are dispatched with `joinDispatchJob` in given order.
// It means that their associated coroutines are executed sequentially
```

It's worth to notice that `JoiningCoroutinesDispatchList` is a `CoThunk` so it's executed in foreground coroutine.

`JoiningCoroutinesDispatchList` has a concurrency flag, that runs associated coroutines concurrently.

```kotlin
val action = JoiningCoroutinesDispatchList(listOf(ActionA, JobActionB, JobActionC), concurrent = true)
// or
val action = ActionA + JobActionB + JobActionC support JoiningCoroutines(concurrent = true)

store.dispatch(action)
// It results in ActionA, JobActionB and JobActionC being dispatched in given order.
// Also, `JoiningCoroutinesDispatchList` waits for coroutines of JobActionB and JobActionC to complete.
```
