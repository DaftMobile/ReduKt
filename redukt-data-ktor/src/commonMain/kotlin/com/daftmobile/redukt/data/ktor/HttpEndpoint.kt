package com.daftmobile.redukt.data.ktor

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse

public interface HttpEndpoint<in Request, RawResponse, out Response> {

    public val requestCreator: HttpRequestCreator<Request>

    public val responseReader: HttpResponseReader<RawResponse>

    public val responseMapper: HttpResponseMapper<Request, RawResponse, Response>

    public val errorMapper: HttpErrorMapper?
}

public object HttpEndpointDefaults {

    public inline fun <reified T> body(): HttpResponseReader<T> = { this.body() }
}

public typealias HttpRequestCreator<Request> = HttpRequestBuilder.(request: Request) -> Unit
public typealias HttpResponseReader<Response> = suspend HttpResponse.() -> Response
public typealias HttpResponseMapper<Request, RawResponse, Response> =
            (request: Request, rawResponse: RawResponse) -> Response
public typealias HttpErrorMapper = (error: Throwable) -> Throwable

public fun <Request, RawResponse, Response> HttpEndpoint(
    requestCreator: HttpRequestCreator<Request>,
    responseReader: HttpResponseReader<RawResponse>,
    responseMapper: HttpResponseMapper<Request, RawResponse, Response>,
    errorMapper: HttpErrorMapper? = null
): HttpEndpoint<Request, RawResponse, Response> = HttpEndpointDefinition(
    requestCreator = requestCreator,
    responseReader = responseReader,
    responseMapper = responseMapper,
    errorMapper = errorMapper
)

public fun <Request, Response> HttpEndpoint(
    requestCreator: HttpRequestCreator<Request>,
    responseReader: HttpResponseReader<Response>,
    errorMapper: HttpErrorMapper? = null
): HttpEndpoint<Request, Response, Response> = HttpEndpointDefinition(
    requestCreator = requestCreator,
    responseReader = responseReader,
    responseMapper = { _, raw -> raw },
    errorMapper = errorMapper
)

public inline fun <Request, reified RawResponse, Response> HttpEndpoint(
    noinline requestCreator: HttpRequestCreator<Request>,
    noinline responseMapper: HttpResponseMapper<Request, RawResponse, Response>,
    noinline errorMapper: HttpErrorMapper? = null
): HttpEndpoint<Request, RawResponse, Response> {
    return HttpEndpoint(
        requestCreator = requestCreator,
        responseReader = HttpEndpointDefaults.body(),
        responseMapper = responseMapper,
        errorMapper = errorMapper
    )
}

public inline fun <Request, reified Response> HttpEndpoint(
    noinline requestCreator: HttpRequestCreator<Request>,
    noinline errorMapper: HttpErrorMapper? = null
): HttpEndpoint<Request, Response, Response> = HttpEndpoint(
    requestCreator = requestCreator,
    responseMapper = { _, raw -> raw },
    errorMapper = errorMapper
)

internal data class HttpEndpointDefinition<in Request, RawResponse, out Response>(
    override val requestCreator: HttpRequestCreator<Request>,
    override val responseReader: HttpResponseReader<RawResponse>,
    override val responseMapper: HttpResponseMapper<Request, RawResponse, Response>,
    override val errorMapper: HttpErrorMapper?
) : HttpEndpoint<Request, RawResponse, Response>
