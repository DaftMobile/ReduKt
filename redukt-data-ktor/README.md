# ReduKt Data Ktor

[![Maven Central](https://img.shields.io/maven-central/v/com.daftmobile.redukt/redukt-data-ktor)](https://mvnrepository.com/artifact/com.daftmobile.redukt/redukt-data-ktor)
[![GitHub](https://img.shields.io/github/license/DaftMobile/ReduKt)](https://github.com/DaftMobile/ReduKt/blob/main/LICENSE)
[![API reference](https://img.shields.io/static/v1?label=api&message=reference&labelColor=gray&color=blueviolet&logo=gitbook&logoColor=white)](https://daftmobile.github.io/ReduKt/redukt-data-ktor/index.html)


ReduKt Data Ktor provides the integration between [ReduKt Data](../redukt-data) and [Ktor Client](https://ktor.io/docs/welcome.html).

### Quick start 

Before reading this, you should be familiar with [ReduKt Data](../redukt-data).

The integration with Ktor is based on association between `DataSourceKey`s and `HttpEndpoint`s. 
Each endpoint has to conform to `Request` and `Response` types defined in a key.

To declare these associations you have to use `HttpDataSourceResolver`:

```kotlin
object DataSources {
    object FetchBook : DataSourceKey<Book.Id, Book>
}

fun createDataSourceResolver() = HttpDataSourceResolver {
    client { // internal HttpClient configuration
        expectSuccess = true
        install(DefaultRequest) {
            url("https://www.your-books-api.com")
        }
    }
    DataSources.FetchBook resolvesTo BookEndpoint
}
```

`HttpEndpoint` is a set of 4 transformation functions:

```kotlin
val BookEndpoint = HttpEndpoint<Book.Id, BookDto, Book>(
    requestCreator = { id -> url.encodedPath = "/api/book/$id" },
    responseReader = { body() },
    responseMapper = BookDto::toBook,
    errorMapper = ::AppException
)
```

* `requestCreator` - transforms your request into HTTP request with 
[HttpRequestBuilder](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html).
* `responseReader` - reads data from
[HttpResponse](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.statement/-http-response/index.html)
as DTO representation. By default, it uses
[HttpResponse.body](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.call/body.html) and it can be omitted.
* `responseMapper` - transforms your DTO representation into domain model.
* `errorMapper` - transforms any error that occurred during an HTTP call into different exception.

Previous example can be simplified:

```kotlin
// we can omit `responseReader`, because HttpResponse.body is the default
val BookEndpoint = HttpEndpoint<Book.Id, BookDto, Book>(
    requestCreator = { id -> url.encodedPath = "/api/book/$id" },
    responseMapper = BookDto::toBook,
    errorMapper = ::AppException
)

// if you don't need any error mapping you can also omit it
val BookEndpoint = HttpEndpoint<Book.Id, BookDto, Book>(
    requestCreator = { id -> url.encodedPath = "/api/book/$id" },
    responseMapper = BookDto::toBook,
)

// if you don't need separate models, you can also omit one of them
val BookEndpoint = HttpEndpoint<String, BookDto>(requestCreator = { id -> path = "/api/books/$id" })
// you have to change your `DataSourceKey` definition to `DataSourceKey<String, BookDto>`
```

`HttpEndpoint`s are easy to test, because you are able to call each transformation function separately in tests with 
any data. However, calling `responseReader` might be a little problematic, because it uses 
[HttpResponse](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.statement/-http-response/index.html), and
it is not easy to instantiate it.
