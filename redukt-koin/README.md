# ReduKt Koin

[![Maven Central](https://img.shields.io/maven-central/v/com.daftmobile.redukt/redukt-koin)](https://mvnrepository.com/artifact/com.daftmobile.redukt/redukt-koin)
[![GitHub](https://img.shields.io/github/license/DaftMobile/ReduKt)](https://github.com/DaftMobile/ReduKt/blob/main/LICENSE)

ReduKt integration with [Koin framework](https://github.com/InsertKoinIO/koin).

### Quick start

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
