package com.algorithmlearning.shared.library

import com.algorithmlearning.shared.library.data.KtorReviewRemote
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KtorReviewRemoteTest {

    @Test
    fun submitPostsConfidenceAndNotes() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(reviewJson, HttpStatusCode.Created)
        }
        val remote = KtorReviewRemote(apiClient(engine, FakeAuthRepository("token-1")))

        val review = remote.submit("problem-1", 3, "Solid.")

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("/api/v1/reviews", request.url.encodedPath)
        val body = (request.body as TextContent).text
        assertContains(body, "\"problemId\":\"problem-1\"")
        assertContains(body, "\"confidence\":3")
        assertEquals(3, review.confidence)
        assertEquals("mvp-1", review.policyVersion)
    }

    @Test
    fun dueReadsTodaysProblemsWithPaging() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse("""[$summaryJson]""")
        }
        val remote = KtorReviewRemote(apiClient(engine))

        val problems = remote.due(page = 2, pageSize = 5)

        val request = assertNotNull(captured)
        assertEquals("/api/v1/reviews/today", request.url.encodedPath)
        assertEquals("2", request.url.parameters["page"])
        assertEquals("5", request.url.parameters["pageSize"])
        assertEquals("problem-1", problems.single().id)
    }

    @Test
    fun historyReadsProblemScopedEvents() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse("""[$reviewJson]""")
        }
        val remote = KtorReviewRemote(apiClient(engine))

        val history = remote.history("problem-1", page = 3, pageSize = 7)

        val request = assertNotNull(captured)
        assertEquals("/api/v1/reviews/history", request.url.encodedPath)
        assertEquals("problem-1", request.url.parameters["problemId"])
        assertEquals("3", request.url.parameters["page"])
        assertEquals("7", request.url.parameters["pageSize"])
        assertEquals("review-1", history.single().id)
    }
}
