package com.daftmobile.redukt.data.ktor

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse

/**
 * Represents a set of transforming functions that is specific for an HTTP endpoint.
 *
 * Usage scheme:
 * 1. [requestCreator] transforms [Request] into [HttpRequestBuilder].
 * 2. Actual HTTP call with Ktor client.
 * 3. [responseReader] reads a [Dto] from [HttpResponse].
 * 4. [responseMapper] transforms [Dto] into [Response].
 * 5. If any exception occurred, it is mapped with [errorMapper] and rethrown.
 */
public sealed interface HttpEndpoint<in Request, Dto, out Response> {

    public val requestCreator: HttpRequestCreator<Request>

    public val responseReader: HttpResponseReader<Dto>

    public val responseMapper: HttpResponseMapper<Request, Dto, Response>

    public val errorMapper: HttpErrorMapper?
}

public typealias HttpRequestCreator<Request> = HttpRequestBuilder.(request: Request) -> Unit
public typealias HttpResponseReader<Response> = suspend HttpResponse.() -> Response
public typealias HttpResponseMapper<Request, Dto, Response> =
            (request: Request, dto: Dto) -> Response
public typealias HttpErrorMapper = (error: Throwable) -> Throwable

/**
 * Creates a [HttpEndpoint] with given transformation functions.
 */
public fun <Request, Dto, Response> HttpEndpoint(
    requestCreator: HttpRequestCreator<Request>,
    responseReader: HttpResponseReader<Dto>,
    responseMapper: HttpResponseMapper<Request, Dto, Response>,
    errorMapper: HttpErrorMapper? = null
): HttpEndpoint<Request, Dto, Response> = HttpEndpointDefinition(
    requestCreator = requestCreator,
    responseReader = responseReader,
    responseMapper = responseMapper,
    errorMapper = errorMapper
)

/**
 * Creates a [HttpEndpoint] with given transformation functions that reads a [Dto] form [HttpResponse]
 * with [HttpResponse.body].
 */
public inline fun <Request, reified Dto, Response> HttpEndpoint(
    noinline requestCreator: HttpRequestCreator<Request>,
    noinline responseMapper: HttpResponseMapper<Request, Dto, Response>,
    noinline errorMapper: HttpErrorMapper? = null
): HttpEndpoint<Request, Dto, Response> {
    return HttpEndpoint(
        requestCreator = requestCreator,
        responseReader = HttpResponse::body,
        responseMapper = responseMapper,
        errorMapper = errorMapper
    )
}

/**
 * Creates a [HttpEndpoint] with given transformation functions, that reads a [Response] form [HttpResponse]
 * with [HttpResponse.body] and returns it without mapping.
 */
public inline fun <Request, reified Response> HttpEndpoint(
    noinline requestCreator: HttpRequestCreator<Request>,
    noinline errorMapper: HttpErrorMapper? = null
): HttpEndpoint<Request, Response, Response> = HttpEndpoint(
    requestCreator = requestCreator,
    responseMapper = { _, raw -> raw },
    errorMapper = errorMapper
)

internal data class HttpEndpointDefinition<in Request, Dto, out Response>(
    override val requestCreator: HttpRequestCreator<Request>,
    override val responseReader: HttpResponseReader<Dto>,
    override val responseMapper: HttpResponseMapper<Request, Dto, Response>,
    override val errorMapper: HttpErrorMapper?
) : HttpEndpoint<Request, Dto, Response>
