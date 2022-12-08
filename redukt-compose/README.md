# ReduKt Compose

ReduKt Compose is a small set of utils to work with ReduKt store in Jetpack Compose.

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

Example below shows how to properly dispatch actions and observe the state from Jetpack Compose.

```kotlin
@Composable
fun TodoListScreen() {
    val dispatch by LocalStore.dispatch
    val todos by LocalStore.select { it.todos }
    LaunchEffect(Unit) { dispatch(TodoAction.Load) }
    Column {
        todos.forEach {
            TodoItemUi(item = it)
        }
    }
}
```

To observe whole state from Compose you can use `LocalStore.state`
