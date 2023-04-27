# ReduKt Compose

[![Maven Central](https://img.shields.io/maven-central/v/com.daftmobile.redukt/redukt-compose)](https://mvnrepository.com/artifact/com.daftmobile.redukt/redukt-compose)
[![GitHub](https://img.shields.io/github/license/DaftMobile/ReduKt)](https://github.com/DaftMobile/ReduKt/blob/main/LICENSE)

ReduKt Compose is a small set of utils to work with ReduKt store in Jetpack Compose.

It depends on [compose-jb,](https://github.com/JetBrains/compose-jb) so it's possible to use it outside of Android.

### Quick start

You have to create a `CompositionLocal` for your store and provide an instance like this:

```kotlin
val LocalStore = localStoreOf<AppState>()

private val store = buildStore { /* ... */ }

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalStore provides store) { /* ... */ }
        }
    }
}
```

Now your store is available from any composable, and you are able to observe state and dispatch actions:

```kotlin
@Composable
fun TodoListScreen() {
    val dispatch = LocalStore.dispatch
    val todos by LocalStore.selectAsState { it.todos }
    LaunchEffect(Unit) { dispatch(TodoAction.Load) }
    Column {
        todos.forEach {
            TodoItemUi(item = it)
        }
    }
}

// or 

@Composable
fun TodoListScreen() = with(LocalStore.current) {
    val todos by selectAsState { it.todos }
    LaunchEffect(Unit) { dispatch(TodoAction.Load) }
    Column {
        todos.forEach {
            TodoItemUi(item = it)
        }
    }
}
```

If `TodoAction.Load` is a `ForegroundJobAction` you might want to cancel it on a composable disposal. In previous example
it won't happen. To automatically cancel foreground coroutine from a `LaunchedEffect` you can just use `joinDispatchJob`:

```kotlin
@Composable
fun TodoListScreen() {
    val joinDispatchJob = LocalStore.joinDispatchJob
    // ...
    LaunchEffect(Unit) { joinDispatchJob(TodoAction.Load) }
    // ...
}

// or

@Composable
fun TodoListScreen() {
    val store = LocalStore.current
    // ...
    LaunchEffect(Unit) { store.joinDispatchJob(TodoAction.Load) }
    // ...
}

```

If you want to achieve this behaviour outside suspendable function, you can use `dispatchJobInHere`:

```kotlin
@Composable
fun TodoListScreen() {
    val dispatchJobInHere = LocalStore.dispatchJobInHere
    // ...
    Button(
        onClick = { dispatchJobInHere(TodoAction.Load) }
    ) {
        // ...   
    }
}

// or 

@Composable
fun TodoListScreen() {
    val store = LocalStore.current
    val scope = rememberCoroutineScope()
    // ...
    Button(
        onClick = { store.dispatchJobIn(TodoAction.Load, scope) }
    ) {
        // ...   
    }
}
```
