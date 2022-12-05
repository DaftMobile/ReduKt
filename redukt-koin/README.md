# ReduKt Koin

ReduKt integration with [Koin framework](https://github.com/InsertKoinIO/koin).

### Examples

You have 2 ways to work with Koin here.

The first one depends on global koin instance initialized by `startKoin { ... }` like this:

```kotlin
fun createStore(): Store<AppState> {
    startKoin {
        single { HttpClient() }
        // ...
    }
    return buildStore {
        // ...
        clousre {
            +GlobalKoinDI
        }
    }
}

fun customMiddleware() = middleware<AppState> {
    val client by koin.inject<HttpClient>()
    // ...
}
```

The second one allows you to inject specific koin application instance:

```kotlin
fun createStore(): Store<AppState> {
    val koinApp = koinApplication {
        single { HttpClient() }
        // ...
    }
    return buildStore {
        // ...
        clousre {
            +KoinApplicationDI(koinApp)
        }
    }
}

fun customMiddleware() = middleware<AppState> {
    val client by koin.inject<HttpClient>()
    // ...
}
```
