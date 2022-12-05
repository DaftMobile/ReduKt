# ReduKt

[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](http://kotlinlang.org)

ReduKt is a [Redux pattern](https://redux.js.org/understanding/thinking-in-redux/three-principles) implementation
adapted to Kotlin and integrated with [coroutines](https://github.com/Kotlin/kotlinx.coroutines)! Besides
the core library, ReduKt comes with ready-to-use extension libraries, that provide solutions for common problems and
help you reduce the boilerplate.

## Materials

Guides and examples are available for each library in their corresponding README files. They are easily accessible from
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
* [ReduKt Thunk](redukt-test) + [ReduKt Thunk Test](redukt-test-thunk) - Redux Thunk adaptation
* [ReduKt Data Source](redukt-data-source) - fetching data from external data sources with generic actions as
  a result.
* [ReduKt Koin](redukt-koin) - integration with [Koin framework](https://github.com/InsertKoinIO/koin).
* [ReduKt Compose](redukt-compose) - integration with Jetpack Compose.
## Multiplatform

This library is highly dependent on [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and therefore
supports the same range of targets.

Targets list is available [here](build-redukt/src/main/kotlin/redukt-lib.gradle.kts).