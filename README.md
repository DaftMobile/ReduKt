# ReduKt

[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](http://kotlinlang.org)

ReduKt is a [Redux pattern](https://redux.js.org/understanding/thinking-in-redux/three-principles) implementation
adapted to Kotlin, integrated with [coroutines](https://github.com/Kotlin/kotlinx.coroutines)! Besides
the core library, ReduKt comes with ready-to-use extension libraries, that provide solutions for common problems and 
help you reduce the boilerplate.

## Libraries

* [ReduKt Core](redukt-core/README.md)
  * Redux store implementation with middlewares support.
  * Wide range of middleware builders.
  * Tools to ensure single-threaded usage.
  * Coroutines support - easy launching/joining/cancelling coroutines logically associated with actions.
  * _Dispatch closure_ - simple & powerful dependency injection mechanism to share objects between middlewares.
* [ReduKt Test](redukt-test/README.md)
  * Dispatch spying tools with wide range of assertions.
  * Middlewares testing tools.
  * TestStore implementation for verifying interactions with a store.
* [ReduKt Thunk](redukt-test/README.md) + [ReduKt Thunk Test](redukt-test-thunk/README.md) - Redux Thunk adaptation
* [ReduKt Data Source](redukt-data-source/README.md) - fetching data from external data sources with generic actions as a result.
* [ReduKt Koin](redukt-koin/README.md) - integration with [Koin framework](https://github.com/InsertKoinIO/koin).
* [ReduKt Insight](redukt-insight/README.md) - logging and time measuring tools.

## Multiplatform

This library is highly dependent on [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and therefore
supports the same range of targets.