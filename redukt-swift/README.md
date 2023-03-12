# ReduKt Swift

ReduKt Swift provides a wrapper for ReduKt store to make accessing it from Swift easier.

# Quick start

Let's assume there is a `buildAppStore` function that creates configured ReduKt Store in `commonMain` source set.

To provide the best experience on the Swift side you should:

1. Hide `buildAppStore` function with `HiddenFromObjC` (available with Kotlin 1.8).
2. Add this library to your darwin source set.
3. Create function that wraps `buildAppStore`:
    ```kotlin
    object IosApp {
        fun buildAppStore() = buildAppStore().toSwiftStore()
   }
    ```
4. Provide `subscribe...` extensions to keep selected types from being lost in Objective-C interop:
    ```kotlin
    fun SwiftStore<AppState>.subscribePosts(onChange: (List<Post>) -> Unit): Disposable = subscribe(PostsSelector, onChange)
    ```

Example below shows how to use ReduKt store from Swift with given adjustments:
```swift
let store = IosApp.shared.buildAppStore()
let subDisposable = store.subscribePosts {
   // process posts updates
}
// ...
val jobDisposable = store.dispatchJob(PostAction.Fetch)
// ...
jobDisposable.dispose()
// ...
subDisposable.dispose()
```

Also, if you are using SwiftUI these samples should be useful:

```swift
struct PostsView: View {
    
    @Environment(\.reduKtStore) var store
    
    var body: some View {
        ObserveOn(store.subscribePosts(onChange:)) { posts in
            // ...
        }
    }
}
```

```swift
private struct ReduKtStoreKey: EnvironmentKey {
    typealias Value = ReduKtStore<AppState>
    
    static var defaultValue: ReduKtStore<AppState> = IosApp.shared.buildAppStore()
}

extension EnvironmentValues {
    var reduKtStore: ReduKtStore<AppState> {
        get { self[ReduKtStoreKey.self] }
        set { self[ReduKtStoreKey.self] = newValue }
    }
}

class Observer<T> : ObservableObject {
    
    @Published private var rawValue: T? = nil
    var value: T { rawValue! }
    private var disposable: ReduKtDisposable? = nil
    
    init(_ subscribe: (@escaping (T) -> Void) -> ReduKtDisposable) {
        disposable = subscribe { [weak self] value in
            self?.rawValue = value
        }
    }
    deinit {
        disposable?.dispose()
    }
}

struct ObserveOn<State, Content>: View where Content: View {
    private let content: (State) -> Content
    @StateObject var observer: Observer<State>
    
    init(
        _ subscriber: @escaping (@escaping (State) -> Void) -> ReduKtDisposable,
        @ViewBuilder _ content: @escaping (State) -> Content
    ) {
        self.content = content
        self._observer = .init(wrappedValue: Observer(subscriber))
    }
    
    var body: some View {
        content(observer.value)
    }
}
```
