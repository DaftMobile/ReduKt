# ReduKt Thunk

Adaptation of [Redux thunk](https://redux.js.org/usage/writing-logic-thunks) in ReduKt.

### Quick start

In ReduKt every thunk is an action that has some logic attached to it and evaluated by `thunkMiddleware`. 
There are 2 types of thunks:

* Associated with regular function
```kotlin
interface ThunkAction<State> : Action {
    fun DispatchScope<State>.execute()
}
```
* Associated with suspending function
```kotlin
interface CoThunkAction<State> : ForegroundJobAction {
    suspend fun DispatchScope<State>.execute()
}
```

Every thunk has `DispatchScope` as `execute` method receiver. It provides `closure`, `dispatch` and `currentState`.

To create a thunk you can simply implement `ThunkAction` or `CoThunkAction` like this:

```kotlin
data class FetchBook(val id: String) : CoThunkAction<AppState> {
    suspend fun DispatchScope<AppState>.execute() {
        val client by koin.instance()
        runCatching { client.get("/book/$id") }
            .onSuccess { dispatch(BookFetchSuccess(it)) }
            .onFailure { dispatch(BookFetchFailed(it)) }
    }
}
```
You can also instantiate it directly with `Thunk` or `CoThunk` classes:

```kotlin
fun fetchBook(id: String): CoThunkAction<AppState> = CoThunk {
    val client by koin.instance()
    runCatching { client.get("/book/$id") }
        .onSuccess { dispatch(BookFetchSuccess(it)) }
        .onFailure { dispatch(BookFetchFailed(it)) }
}
```

`Thunk` and `CoThunk` are open classes, so you might create subclasses for them:

```kotlin
data class FetchBook(val id: String) : CoThunk<AppState>({
    val client by koin.instance()
    runCatching { client.get("/book/$id") }
        .onSuccess { dispatch(BookFetchSuccess(it)) }
        .onFailure { dispatch(BookFetchFailed(it)) }
})
```