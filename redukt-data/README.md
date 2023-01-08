# ReduKt Data

ReduKt Data provides a generic flow of fetching asynchronous data.

## Guide

1. [Data sources](#data-sources)
2. [Resolving data sources](#resolving-data-sources)
3. [Calling data sources](#calling-data-sources)
4. [Cancelling calls](#cancelling-calls)
5. [Handling responses](#handling-responses)

### Data sources

A data source is any object that provides a response to a request, most of the time in an asynchronous way.
It has to implement `DataSource` interface. Example below shows how to implement it with Ktor:

```kotlin
data class FetchBookDataSource(
    val client: HttpClient,
) : DataSource<String, Book> {

    override suspend fun call(request: String): Book {
        return client
            .get<BookDto>("/book/$request")
            .toBook()
    }
}
```

### Resolving data sources

The first thing you have to do to make your `DataSource` "visible" by ReduKt Data is to create a key:

```kotlin
object DataSources {
    object FetchBook : DataSourceKey<String, Book>
}
```

Then you have to create a `DataSourceResolver` by `DataSourceResolver` function (recommended) or implement it yourself.
Example below shows the first way:

```kotlin
fun createDataSourceResolver(httpClient: HttpClient) = DataSourceResovler {
    DataSources.FetchBook resolvedBy { FetchBookDataSource(client) }
}
```

Be aware that function passed to `resolvedBy` is called every time your `DataSource` is resolved.

Now you have to add `DataSourceResolver` to the store closure:

```kotlin
fun store(httpClient: HttpClient) = buildStore {
    // ...
    closure {
        +createDataSourceResolver(httpClient)
        // ...
    }
}
```

### Calling data sources

The `dataSourceMiddleware` is responsible for calling data sources, so it has to be added to a store:

```kotlin
fun store(httpClient: HttpClient) = buildStore {
    middlewares {
        // ...
        +dataSourceMiddleware
        // ...
    }
    // ...
}
```

To trigger a data source call you have to dispatch `DataSourceCall`:

```kotlin
store.dispatch(DataSourceCall(key = DataSources.FetchBook, request = "book-1"))
```

It results in:

1. `DataSources.FetchBook` is resolved with `DataSourceResolver` from the closure. If `DataSourceResolver`
   or `DataSource` with given key is missing, exception is thrown and flow ends here.
2. `DataSourceAction(DataSources.FetchBook, DataSourcePayload.Started("book-1"))` is dispatched.
3.  A Foreground coroutine is launched. `dispatch` method returns here. The rest of the flow happens in the coroutine.
4. `DataSource` is called with "book-1".
    * If it fails, `DataSourceAction(DataSources.FetchBook, DataSourcePayload.Failure("book-1", exception))` is
      dispatched.
    * If it returns response properly, 
   `DataSourceAction(DataSources.FetchBook, DataSourcePayload.Success("book-1", response))` is dispatched.

### Cancelling calls

To cancel a data source call, you have to cancel the foreground coroutine that hosts it:

```kotlin
val job = store.dispatchJob(DataSourceCall(key = DataSources.FetchBook, request = "book-1"))
// ...
job.cancel()
```

Cancellation results
in `DataSourceAction(DataSources.FetchBook, DataSourcePayload.Failure("book-1", cancellationException))`.

### Handling responses

All events related to data sources are represented by `DataSourceAction`.
Every action contains a `DataSourceKey` and a `DataSourcePayload`.
Payload has 3 variants:

* `DataSourcePayload.Started` - dispatched on start of the call.
* `DataSourcePayload.Success` - dispatched on successful call.
* `DataSourcePayload.Failure` - dispatched on failed call.

The suggested way to handle these events is to use the `createDataSourceReducer`:

```kotlin

object DataSources {
    object FetchAllBooks : DataSourceKey<Unit, List<Book>>
}

data class DataHolder<Data>(
    val isLoading: Boolean,
    val error: Throwable?,
    val data: Data,
)

val booksReducer: Reducer<DataHolder<List<Book>>> = createDataSourceReducer(
    key = DataSources.FetchAllBooks,
    onStarted = { holder, _ -> holder.copy(isLoading = true) },
    onSuccess = { holder, (_, books) -> holder.copy(isLoading = false, error = null, data = books) },
    onFailure = { holder, (_, error) -> holder.copy(error = error) }
)
```