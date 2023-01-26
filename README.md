# ReduKt

[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](http://kotlinlang.org)

ReduKt is a [Redux pattern](https://redux.js.org/understanding/thinking-in-redux/three-principles) adaptation in Kotlin, 
integrated with [coroutines](https://github.com/Kotlin/kotlinx.coroutines), testable and multiplatform. 
Implemented store provides easy & powerful way of extending its functionality - _dispatch closure_.
It is used to deliver customizable extensions that help you reduce the boilerplate according to your needs!
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
        +GlobalKoinDI
    }
}

// Fast middlewares creation with predefined builders and simplified API!
val debugMiddleware = translucentMiddleware<AppState> { action -> println(it) }

val postsMiddleware = consumingMiddleware<AppState, PostAction.Fetch> {
    // Easy access to external APIs with DispatchClosure
    val client by koin.inject<HttpClient>()
    // Coroutines launched easily!
    launchForeground {
        runCatching { client.get("/posts").body<List<Post>>() }
            .onSuccess { dispatch(PostAction.OnSuccess(it)) }
            .onFailure { dispatch(PostAction.OnFailure(it)) }
    }
}

// Painless control over an associated coroutine!
val job = store.dispatchJob(PostAction.Fetch)
// ..
job.cancel()

// Driven by the Flow!
store.state.collect { println("New state: $it") }
```

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

## Materials

Guides and examples are available for each library in their corresponding README files. They are easily accessible from
[libraries section](#libraries).

API reference has not been published yet. It's only available from the source code.

## Multiplatform

This library is highly dependent on [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and therefore
supports the same range of targets.

Targets list is available [here](build-redukt/src/main/kotlin/redukt-lib.gradle.kts).
