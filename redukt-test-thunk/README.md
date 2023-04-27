# ReduKt Test Thunk

[![Maven Central](https://img.shields.io/maven-central/v/com.daftmobile.redukt/redukt-test-thunk)](https://mvnrepository.com/artifact/com.daftmobile.redukt/redukt-test-thunk)
[![GitHub](https://img.shields.io/github/license/DaftMobile/ReduKt)](https://github.com/DaftMobile/ReduKt/blob/main/LICENSE)

ReduKt Test Thunk provides tools to test thunks. It's an extension of [ReduKt Test](../redukt-test) and
provides very similar API.

### Quick start

Testing thunks is very similar to testing middlewares
described [here](../redukt-test/README.md#testing-middlewares-basics),
and you should start there. Simple examples below shows thunk testing flow:

* For `Thunk`:

```kotlin
val CustomThunk = Thunk<AppState> {
    if (currentState.condition) dispatch(ActionA)
}

class CustomThunkTest {
    private val tester = CustomThunk.tester(initialState = AppState())

    @Test
    fun test() = tester.test {
        // given
        currentState = AppState(condition = true)
        // when
        testExecute()
        // then
        assertSingleActionEquals(ActionA)
    }
}
```

* For `CoThunk` (requires `runTest`
from [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test)):

```kotlin
val CustomCoThunk = CoThunk<AppState> {
    if (currentState.condition) {
        delay(1_000)
        dispatch(ActionA)
    }
}

class CustomCoThunkTest {
    private val tester = CustomCoThunk.tester(initialState = AppState())

    @Test
    fun test() = runTest {
        tester.test {
            // given
            currentState = AppState(condition = true)
            // when
            testExecute() // suspends until thunk function completes
            // then
            assertSingleActionEquals(ActionA)
        }
    }
}
```
