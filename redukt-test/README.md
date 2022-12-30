# ReduKt Test

ReduKt Test provides tools to test ReduKt components.

## Guide

1. [Testing middlewares basics](#testing-middlewares-basics)
2. [Testing foreground coroutines](#testing-foreground-coroutines)
3. [Testing interactions with a store](#testing-interactions-with-a-store)


### Testing middlewares basics

To test a middleware you have to create a `MiddlewareTester` in your test class:

```kotlin
class CustomMiddlewareTest {
    private val tester = customMiddleware().tester(initialState = AppState(), initialClosure = EmptyDispatchClosure)
    // ...
}
```

To perform a test on given middleware you have to call `test` method. Each call initiates middleware separately.

Tester provides operations to change state or closure dynamically like this:

```kotlin
@Test
fun test() = tester.test {
    state = AppState(hasNetwork = false)
    clousre += GlobalKoinDi
    // ...
}
```

> **Warning**: You should prefer `+=` to insert elements to closure.
> Using `=` entirely replaces initial closure, which contains mocks important for foreground coroutines mechanism.

This properties might be changed in response to actions dispatched by a middleware under test:

```kotlin
@Test
fun test() = tester.test {
    onDispatch { action ->
        when (action) {
            is FetchBook -> state = AppState(books = listOf(Book()))
        }
    }
    // ...
}
```

When test conditions are configured, you can pass an action to trigger middleware logic:

```kotlin
@Test
fun test() = tester.test {
    // ...
    testAction(ActionA)
    // ...
}
```

Every action that is dispatched by a middleware is inserted into `unverified` queue. 
Most of the asserting functions pull actions from the queue and verify them.
Every tester works in strict mode by default. It means you have to verify or skip all of them explicitly.

```kotlin
@Test
fun testA() = tester.test {
    // ...
    testAction(ActionA)
    assertSingleActionEquals(ActionB) // expects single action dispatched by middleware under test
}

@Test
fun testB() = tester.test {
    // ...
    testAction(ActionB)
    assertActionEquals(ActionC) // expects ActionC to be dispatched first by middleware under test
    assertActionEquals(ActionD) // expects ActionD to be dispatched last by middleware under test
}

@Test
fun testC() = tester.test {
    // ...
    testAction(ActionC)
    assertNoAction() // expects no actions dispatched by middleware
}

@Test
fun testD() = tester.test {
    // ...
    testAction(ActionD)
    skipOthers() // skip all remaining actions in the queue
}
```

You can disable strict mode for tester like this:

```kotlin
class CustomMiddlewareTest {
    private val tester = customMiddleware().tester(initialState = AppState(), strict = false)
    // ...
}
```

You can also disable strict mode for a single test like this:

```kotlin
@Test
fun test() = tester.test(strict = false) {
   // ...
}
```

### Testing foreground coroutines

You can use middleware tester to test foreground coroutines logic. 
It's very similar to testing regular middleware logic, but you have to use `testJobAction` extension and 
your test must be able to suspend its execution. You can achieve this with `runTest` from 
[kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test) like this:

```kotlin
@Test
fun test() = runTest {
    tester.test {
        testJobAction(JobActionA) // it suspends until foreground coroutine completes
    }
}
```

### Testing interactions with a store

ReduKt Test contains a `TestStore` that can be injected into components that depends on a store.
It provides a similar API to a [middleware tester](#testing-middlewares-basics). 

You can dynamically change state,
closure and reactions on dispatch.

```kotlin
val store = TestStore(initialState = AppState(hasNetowork = false), initialClosure = EmptyDispatchClosure)

store.state.value = AppState(hasNetwork = true)
store.closure = GlobalKoinDi
store.onDispatch {
    // ...
}
```

You can also verify dispatched actions:

```kotlin
store.test {
    assertActionEquals(ActionA) // expects ActionA to be dispatched first with this store
    assertActionEquals(ActionB) // expects ActionB to be dispatched last with this store
}
```

`TestStore` is strict by default so every `test` call has to verify every dispatched action. You can disable this behaviour
by `strict` param like this:

```kotlin
val store = TestStore(initialState = AppState(), strict = false) // for every test call

store.test(strict = false) { // for this test call
    // ... 
}
```