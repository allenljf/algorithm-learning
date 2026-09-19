package com.algorithmlearning.shared.auth

import com.algorithmlearning.shared.auth.data.KtorAuthRemote
import com.algorithmlearning.shared.auth.data.applyAuthDefaults
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.time.Instant

class KtorAuthRemoteTest {

    private val userJson = """{"id":"user-1","email":"a@test.dev"}"""
    private val sessionJson =
        """{"user":$userJson,"accessToken":"access-1","expiresAt":"2026-01-01T00:15:00Z"}"""

    private fun remote(
        captured: (HttpRequestData) -> Unit = {},
        response: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): KtorAuthRemote {
        val engine = MockEngine { request ->
            captured(request)
            response(request)
        }
        return KtorAuthRemote(HttpClient(engine) { applyAuthDefaults() }, "http://test")
    }

    private fun MockRequestHandleScope.json(
        responseBody: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): HttpResponseData =
        respond(
            content = responseBody,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )

    @Test
    fun loginPostsCredentialsAndMapsTheSession() = runTest {
        var captured: HttpRequestData? = null
        val remote = remote({ captured = it }) { json(sessionJson) }

        val session = remote.login("a@test.dev", "password1234")

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("/api/v1/auth/login", request.url.encodedPath)
        val body = (request.body as TextContent).text
        assertEquals(true, body.contains("\"email\":\"a@test.dev\""))
        assertEquals(true, body.contains("\"password\":\"password1234\""))
        assertEquals("user-1", session.user.id)
        assertEquals("a@test.dev", session.user.email)
        assertEquals("access-1", session.accessToken)
        assertEquals(Instant.parse("2026-01-01T00:15:00Z"), session.expiresAt)
    }

    @Test
    fun registerAcceptsTheCreatedStatus() = runTest {
        val remote = remote { json(sessionJson, HttpStatusCode.Created) }

        val session = remote.register("a@test.dev", "password1234")

        assertEquals("access-1", session.accessToken)
    }

    @Test
    fun refreshMapsTheRotatedAccessToken() = runTest {
        var captured: HttpRequestData? = null
        val remote = remote({ captured = it }) {
            json("""{"accessToken":"access-2","expiresAt":"2026-01-01T00:30:00Z"}""")
        }

        val token = remote.refresh()

        val request = assertNotNull(captured)
        assertEquals("/api/v1/auth/refresh", request.url.encodedPath)
        assertEquals("access-2", token.value)
        assertEquals(Instant.parse("2026-01-01T00:30:00Z"), token.expiresAt)
    }

    @Test
    fun currentUserSendsABearerToken() = runTest {
        var captured: HttpRequestData? = null
        val remote = remote({ captured = it }) { json(userJson) }

        val user = remote.currentUser("access-9")

        val request = assertNotNull(captured)
        assertEquals("/api/v1/auth/me", request.url.encodedPath)
        assertEquals("Bearer access-9", request.headers[HttpHeaders.Authorization])
        assertEquals("user-1", user.id)
    }

    @Test
    fun logoutPostsToTheLogoutRoute() = runTest {
        var captured: HttpRequestData? = null
        val remote = remote({ captured = it }) {
            respond(content = "", status = HttpStatusCode.NoContent)
        }

        remote.logout()

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("/api/v1/auth/logout", request.url.encodedPath)
    }

    @Test
    fun loginUnauthorizedMapsToInvalidCredentials() = runTest {
        val remote = remote { json("", HttpStatusCode.Unauthorized) }

        val failure = assertFailsWith<AuthFailure> { remote.login("a@test.dev", "password1234") }

        assertEquals(AuthFailureKind.INVALID_CREDENTIALS, failure.kind)
    }

    @Test
    fun refreshUnauthorizedMapsToUnauthorized() = runTest {
        val remote = remote { json("", HttpStatusCode.Unauthorized) }

        val failure = assertFailsWith<AuthFailure> { remote.refresh() }

        assertEquals(AuthFailureKind.UNAUTHORIZED, failure.kind)
    }

    @Test
    fun rateLimitedMapsToRateLimited() = runTest {
        val remote = remote { json("", HttpStatusCode.TooManyRequests) }

        val failure = assertFailsWith<AuthFailure> { remote.login("a@test.dev", "password1234") }

        assertEquals(AuthFailureKind.RATE_LIMITED, failure.kind)
    }

    @Test
    fun serverErrorMapsToServer() = runTest {
        val remote = remote { json("", HttpStatusCode.InternalServerError) }

        val failure = assertFailsWith<AuthFailure> { remote.login("a@test.dev", "password1234") }

        assertEquals(AuthFailureKind.SERVER, failure.kind)
    }

    @Test
    fun malformedPayloadMapsToUnexpected() = runTest {
        val remote = remote { json("{not-json", HttpStatusCode.OK) }

        val failure = assertFailsWith<AuthFailure> { remote.login("a@test.dev", "password1234") }

        assertEquals(AuthFailureKind.UNEXPECTED, failure.kind)
    }
}
