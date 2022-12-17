# ReduKt Test

ReduKt test provides tools to test ReduKt components. 

### Testing middlewares basics

To test a middleware you have to create a `MiddlewareTester` in your test class:

```kotlin
class CustomMiddlewareTest {
    
    private val tester = customMiddleware().tester(initialState = AppState())
    // ...
}
```

To perform a single test on a middleware use `test`. It provides necessary operations to set up preconditions,
pass action to a middleware and check the results.
```kotlin
@Test
fun testExample() = tester.test {
    // given
    state = AppState(condition = false)
    // when
    testAction(CustomAction)
    // then
    TODO()
}
```
