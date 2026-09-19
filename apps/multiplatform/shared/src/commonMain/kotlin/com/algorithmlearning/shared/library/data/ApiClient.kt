package com.algorithmlearning.shared.library.data

import com.algorithmlearning.shared.auth.AuthRepository
import com.algorithmlearning.shared.library.ApiFailure
import com.algorithmlearning.shared.library.ApiFailureKind
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

/**
 * Shared Ktor plumbing for the frozen library API: it attaches the in-memory
 * bearer token, retries exactly once after a `401` (invalidating the token so
 * the auth repository refreshes), and translates failures into [ApiFailure].
 */
class ApiClient(
    val client: HttpClient,
    baseUrl: String,
    private val tokens: AuthRepository,
) {
    val root: String = baseUrl.trimEnd('/') + "/api/v1"

    /**
     * Executes [request] with a valid access token. A `401` triggers one token
     * invalidation, refresh, and retry before surfacing [ApiFailureKind.UNAUTHORIZED].
     */
    suspend fun authorized(request: suspend (String) -> HttpResponse): HttpResponse {
        var token = tokens.accessToken()
            ?: throw ApiFailure(ApiFailureKind.UNAUTHORIZED, statusCode = 401)
        var response = perform(request, token)
        if (response.status == HttpStatusCode.Unauthorized) {
            tokens.invalidateAccessToken()
            token = tokens.accessToken()
                ?: throw ApiFailure(ApiFailureKind.UNAUTHORIZED, statusCode = 401)
            response = perform(request, token)
        }
        response.requireSuccess()
        return response
    }

    /** Wraps body decoding and mapping so domain/parse errors become [ApiFailure]. */
    suspend fun <T> decode(block: suspend () -> T): T = try {
        block()
    } catch (failure: ApiFailure) {
        throw failure
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Throwable) {
        throw ApiFailure(ApiFailureKind.UNEXPECTED, cause = error)
    }

    private suspend fun perform(request: suspend (String) -> HttpResponse, token: String): HttpResponse = try {
        request(token)
    } catch (failure: ApiFailure) {
        throw failure
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Throwable) {
        throw ApiFailure(ApiFailureKind.NETWORK, cause = error)
    }
}

internal fun HttpRequestBuilder.bearer(token: String) {
    header(HttpHeaders.Authorization, "Bearer $token")
}

internal suspend fun HttpResponse.requireSuccess() {
    if (status.isSuccess()) return
    val problem = runCatching { body<ProblemDto>() }.getOrNull()
    throw ApiFailure(
        kind = when (status.value) {
            401, 403 -> ApiFailureKind.UNAUTHORIZED
            404 -> ApiFailureKind.NOT_FOUND
            409 -> ApiFailureKind.CONFLICT
            400, 422 -> ApiFailureKind.VALIDATION
            429 -> ApiFailureKind.RATE_LIMITED
            in 500..599 -> ApiFailureKind.SERVER
            else -> ApiFailureKind.UNEXPECTED
        },
        statusCode = status.value,
        code = problem?.code,
        message = problem?.detail ?: problem?.title,
        fieldErrors = problem?.fieldErrors ?: emptyMap(),
    )
}
