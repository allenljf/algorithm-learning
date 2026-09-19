package com.algorithmlearning.shared.library

import com.algorithmlearning.shared.auth.AuthRepository
import com.algorithmlearning.shared.auth.AuthSession
import com.algorithmlearning.shared.auth.data.applyAuthDefaults
import com.algorithmlearning.shared.library.data.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

/** Token seam for [ApiClient] tests; only the token methods are exercised. */
class FakeAuthRepository(
    initialToken: String? = "token-1",
    private val replacementToken: String? = null,
) : AuthRepository {
    var token: String? = initialToken
    var invalidations: Int = 0

    override suspend fun accessToken(): String? = token

    override suspend fun invalidateAccessToken() {
        invalidations++
        token = replacementToken
    }

    override suspend fun login(email: String, password: String): AuthSession = error("unused")

    override suspend fun register(email: String, password: String): AuthSession = error("unused")

    override suspend fun restore(): AuthSession? = error("unused")

    override suspend fun logout() = error("unused")
}

internal fun apiClient(
    engine: MockEngine,
    tokens: AuthRepository = FakeAuthRepository(),
    baseUrl: String = "http://test",
): ApiClient = ApiClient(
    client = HttpClient(engine) { applyAuthDefaults() },
    baseUrl = baseUrl,
    tokens = tokens,
)

internal fun MockRequestHandleScope.jsonResponse(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
): HttpResponseData = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, "application/json"),
)
