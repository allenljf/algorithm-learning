package com.algorithmlearning.shared.library

import com.algorithmlearning.shared.library.data.ApiClient
import com.algorithmlearning.shared.library.data.bearer
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ApiClientTest {

    private suspend fun getRequest(client: ApiClient, path: String = "/dashboard") =
        client.authorized { token ->
            client.client.get("${client.root}$path") { bearer(token) }
        }

    @Test
    fun authorizedAttachesTheBearerToken() = runTest {
        var authorization: String? = null
        val engine = MockEngine { request ->
            authorization = request.headers[HttpHeaders.Authorization]
            jsonResponse("{}")
        }
        val client = apiClient(engine, FakeAuthRepository(initialToken = "token-1"))

        getRequest(client)

        assertEquals("Bearer token-1", authorization)
    }

    @Test
    fun aMissingTokenFailsWithoutSendingARequest() = runTest {
        var requests = 0
        val engine = MockEngine {
            requests++
            jsonResponse("{}")
        }
        val client = apiClient(engine, FakeAuthRepository(initialToken = null))

        val failure = assertFailsWith<ApiFailure> { getRequest(client) }

        assertEquals(ApiFailureKind.UNAUTHORIZED, failure.kind)
        assertEquals(0, requests)
    }

    @Test
    fun a401RetriesOnceWithARefreshedToken() = runTest {
        val seen = mutableListOf<String?>()
        var requests = 0
        val engine = MockEngine { request ->
            requests++
            seen += request.headers[HttpHeaders.Authorization]
            if (requests == 1) jsonResponse("", HttpStatusCode.Unauthorized) else jsonResponse("{}")
        }
        val tokens = FakeAuthRepository(initialToken = "stale", replacementToken = "fresh")
        val client = apiClient(engine, tokens)

        val response = getRequest(client)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, requests)
        assertEquals(1, tokens.invalidations)
        assertEquals(listOf<String?>("Bearer stale", "Bearer fresh"), seen)
    }

    @Test
    fun aPersistent401SurfacesUnauthorized() = runTest {
        val engine = MockEngine { jsonResponse("", HttpStatusCode.Unauthorized) }
        val tokens = FakeAuthRepository(initialToken = "stale", replacementToken = "fresh")
        val client = apiClient(engine, tokens)

        val failure = assertFailsWith<ApiFailure> { getRequest(client) }

        assertEquals(ApiFailureKind.UNAUTHORIZED, failure.kind)
        assertEquals(1, tokens.invalidations)
    }

    @Test
    fun notFoundMapsToNotFoundAndReadsTheProblemCode() = runTest {
        val engine = MockEngine { jsonResponse("""{"code":"not_found"}""", HttpStatusCode.NotFound) }
        val client = apiClient(engine)

        val failure = assertFailsWith<ApiFailure> { getRequest(client) }

        assertEquals(ApiFailureKind.NOT_FOUND, failure.kind)
        assertEquals("not_found", failure.code)
        assertEquals(404, failure.statusCode)
    }

    @Test
    fun validationCarriesFieldErrors() = runTest {
        val engine = MockEngine {
            jsonResponse(
                """{"code":"validation_error","fieldErrors":{"title":["must not be blank"]}}""",
                HttpStatusCode.UnprocessableEntity,
            )
        }
        val client = apiClient(engine)

        val failure = assertFailsWith<ApiFailure> { getRequest(client) }

        assertEquals(ApiFailureKind.VALIDATION, failure.kind)
        assertEquals(listOf("must not be blank"), failure.fieldErrors["title"])
    }

    @Test
    fun aTransportFailureMapsToNetwork() = runTest {
        val engine = MockEngine { throw RuntimeException("boom") }
        val client = apiClient(engine)

        val failure = assertFailsWith<ApiFailure> { getRequest(client) }

        assertEquals(ApiFailureKind.NETWORK, failure.kind)
    }

    @Test
    fun aMappingFailureMapsToUnexpected() = runTest {
        val client = apiClient(MockEngine { jsonResponse("{}") })

        val failure = assertFailsWith<ApiFailure> {
            client.decode<Unit> { throw IllegalStateException("bad payload") }
        }

        assertEquals(ApiFailureKind.UNEXPECTED, failure.kind)
    }
}
