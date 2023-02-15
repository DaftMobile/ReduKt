# ReduKt

[![Kotlin](https://img.shields.io/badge/kotlin-1.8.0-blue.svg?logo=kotlin)](http://kotlinlang.org) [![build](https://github.com/lupuuss/ReduKt/actions/workflows/gradle.yml/badge.svg)](https://github.com/lupuuss/ReduKt/actions/workflows/gradle.yml)

ReduKt is a [Redux pattern](https://redux.js.org/understanding/thinking-in-redux/three-principles) adaptation in Kotlin,
integrated with [coroutines](https://github.com/Kotlin/kotlinx.coroutines), testable and multiplatform.
Implemented store provides easy & powerful way of extending its functionality - _dispatch closure_.
It is used to deliver customizable extensions that help you reduce the boilerplate according to your needs!

## Materials
If you want to see basic examples, go to [overview section](#overview).
More in-depth guides and examples are available for each library in their corresponding README files. They are easily accessible from
[libraries section](#libraries).

API reference has not been published yet. It's only available from the source code.

## Libraries

* [ReduKt Core](redukt-core)
    * Redux store implementation with middlewares support.
    * Wide range of middleware builders.
    * Tools to ensure single-threaded usage.
    * Coroutines support - easy launching/joining/cancelling coroutines logically associated with actions.
    * _Dispatch closure_ - simple mechanism to extend store's functionality (e.g. integration with DI frameworks)
* [ReduKt Test](redukt-test)
    * Dispatch spying tools with wide range of assertions.
    * Middlewares testing tools.
    * TestStore implementation for verifying interactions with a store.
* [ReduKt Thunk](redukt-thunk) + [ReduKt Thunk Test](redukt-test-thunk) - Redux Thunk adaptation
* [ReduKt Data](redukt-data) - fetching data from external data sources with generic actions as
  a result.
* [ReduKt Koin](redukt-koin) - integration with [Koin framework](https://github.com/InsertKoinIO/koin).
* [ReduKt Compose](redukt-compose) - integration with Jetpack Compose.
* [ReduKt Swift](redukt-swift) - store wrapper that provides better experience on the Swift side.

## Overview

Every Redux app starts with creating a store. In ReduKt here is how it's done:

```kotlin
data class AppState(val posts: List<Post> = emptyList())

fun appReducer(state: AppState, action: Action) = AppState(posts = postsReducer(state.posts, action))

// More flexible store creation with DSL!
val store = buildStore {
    AppState() reducedBy ::appReducer
    middlewares {
        if (IS_DEBUG) +debugMiddleware
        +postsMiddleware
    }
    // Extending store's functionality with DispatchClosure mechanism!
    closure {
        +GlobalKoinDI // Ready-to-use integration with Koin!
    }
}
```

State changes are delivered by the StateFlow:
```kotlin
// Collecting whole state...
store.state.collect { /* ... */ }
// ...or just a part of it!
store.select { it.posts }.collect { /* ... */ }
```

Customizable selectors that enables performance improvements:
```kotlin
val totalProductsPriceSelector = createSelector(
  stateEquality = { old, new -> old.products == new.products },
  selector = { it.products.sumBy(Product::price) }
)
store.select(totalProductsPriceSelector).collect { 
    // Total prices is recalculated only if products have changed and when value is really needed!
}
```

Declaring middlewares comes with predefined utils:
```kotlin
// Standard middleware declaration. In comparison to Redux JS it has only 1 nested lambda.
// All the operations are available from lambda receiver and do not require declaring them as parameters!
val debugMiddleware: Middleware<AppState> = {
    { action ->
        println(action)
        next(action)
    }
}
// You can declare it shorter with `middleware` util
val debugMiddleware = middleware<AppState> { action ->
    println(action)
    next(action)
}
// ...or even shorter with `translucentMiddleware`
val debugMiddleware = translucentMiddleware<AppState> { action -> println(action) }
```

Launching and controlling coroutines is integrated with the store:
```kotlin
fun fetchPostsMiddleware(client: HttpClient) = consumingMiddleware<AppState, PostAction.FetchAll> {
    launchForeground {
        runCatching { client.get("/posts") }
            .onSuccess { dispatch(PostAction.FetchSuccess(it)) }
            .onFailure { dispatch(PostAction.FetchFailure(it)) }
    }
}
// ...
val job = store.dispatchJob(PostAction.FetchAll)
// ...
job.cancel()
```

Http client instance might be injected thanks to [ReduKt Koin](redukt-koin):
```kotlin
val fetchPostsMiddleware = consumingMiddleware<AppState, PostAction.FetchAll> {
    val httpClient by koin.inject<HttpClient>()
    launchForeground {
        // ...
    }
}
```

You might reduce amount of required middlewares with [ReduKt Thunk](redukt-thunk):
```kotlin
object FetchAllPosts : CoThunk<AppState>({
    val httpClient by koin.inject<HttpClient>()
    runCatching { client.get("/posts") }
        .onSuccess { dispatch(PostAction.FetchSuccess(it)) }
        .onFailure { dispatch(PostAction.FetchFailure(it)) }
})
// ...
val job = store.dispatchJob(FetchAllPosts)
// ...
job.cancel()
```

To reduce even more code with asynchronous data sources, you can use [ReduKt Data](redukt-data)!

## Multiplatform

This library is highly dependent on [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and therefore
supports the same range of targets.

Targets list is available [here](build-redukt/src/main/kotlin/redukt-lib.gradle.kts).
