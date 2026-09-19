package com.algorithmlearning.shared.library

import com.algorithmlearning.shared.library.data.KtorProblemRemote
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class KtorProblemRemoteTest {

    @Test
    fun listSendsEveryFilterAndMapsThePage() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(
                """{"items":[$summaryJson],"page":2,"pageSize":10,"totalItems":11,"totalPages":2}""",
            )
        }
        val remote = KtorProblemRemote(apiClient(engine, FakeAuthRepository("token-1")))

        val page = remote.list(
            ProblemQuery(
                query = "sum",
                difficulty = ProblemDifficulty.EASY,
                platform = ProblemPlatform.HACKER_RANK,
                tagIds = listOf("t1", "t2"),
                reviewStatus = ReviewStatus.DUE,
                sort = ProblemSort.TITLE_ASC,
                page = 2,
                pageSize = 10,
            ),
        )

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Get, request.method)
        assertEquals("/api/v1/problems", request.url.encodedPath)
        assertEquals("Bearer token-1", request.headers[HttpHeaders.Authorization])
        assertEquals("sum", request.url.parameters["q"])
        assertEquals("easy", request.url.parameters["difficulty"])
        assertEquals("hacker_rank", request.url.parameters["platform"])
        assertEquals("due", request.url.parameters["reviewStatus"])
        assertEquals("titleAsc", request.url.parameters["sort"])
        assertEquals(listOf("t1", "t2"), request.url.parameters.getAll("tagId"))
        assertEquals("2", request.url.parameters["page"])
        assertEquals("10", request.url.parameters["pageSize"])
        assertEquals(ProblemPlatform.HACKER_RANK, page.items.single().platform)
        assertEquals(ReviewStatus.NEVER_REVIEWED, page.items.single().review.status)
        assertEquals(2, page.totalPages)
    }

    @Test
    fun listOmitsUnsetFiltersAndDefaults() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse("""{"items":[],"page":1,"pageSize":20,"totalItems":0,"totalPages":0}""")
        }
        val remote = KtorProblemRemote(apiClient(engine))

        remote.list(ProblemQuery())

        val request = assertNotNull(captured)
        assertEquals(null, request.url.parameters["q"])
        assertEquals(null, request.url.parameters["difficulty"])
        assertEquals(null, request.url.parameters["platform"])
        assertEquals(null, request.url.parameters["reviewStatus"])
        assertEquals("updatedDesc", request.url.parameters["sort"])
        assertEquals(emptyList(), request.url.parameters.getAll("tagId").orEmpty())
    }

    @Test
    fun getMapsTheDetailAndItsSolutions() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(detailJson)
        }
        val remote = KtorProblemRemote(apiClient(engine, FakeAuthRepository("token-1")))

        val detail = remote.get("problem-1")

        assertEquals("/api/v1/problems/problem-1", assertNotNull(captured).url.encodedPath)
        assertEquals("problem-1", detail.summary.id)
        assertEquals(ProblemPlatform.LEETCODE, detail.summary.platform)
        assertEquals("O(n)", detail.timeComplexity)
        assertEquals(SolutionLanguage.KOTLIN, detail.solutions.single().language)
        assertEquals("Map complements.", detail.solutions.single().explanation)
    }

    @Test
    fun createPostsTheFullWriteBody() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(detailJson, HttpStatusCode.Created)
        }
        val remote = KtorProblemRemote(apiClient(engine, FakeAuthRepository("token-1")))

        remote.create(
            ProblemWrite(
                title = "Two Sum",
                platform = ProblemPlatform.HACKER_RANK,
                difficulty = ProblemDifficulty.EASY,
                tagIds = listOf("t1"),
            ),
        )

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("/api/v1/problems", request.url.encodedPath)
        val body = (request.body as TextContent).text
        assertContains(body, "\"platform\":\"hacker_rank\"")
        assertContains(body, "\"difficulty\":\"easy\"")
        assertContains(body, "\"tagIds\":[\"t1\"]")
        assertContains(body, "\"externalUrl\":null")
        assertContains(body, "\"keyInsight\":null")
    }

    @Test
    fun replacePutsTheWriteBody() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(detailJson)
        }
        val remote = KtorProblemRemote(apiClient(engine))

        remote.replace("problem-1", ProblemWrite("Two Sum", ProblemPlatform.LEETCODE, ProblemDifficulty.EASY))

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Put, request.method)
        assertEquals("/api/v1/problems/problem-1", request.url.encodedPath)
    }

    @Test
    fun deleteSendsADelete() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse("", HttpStatusCode.NoContent)
        }
        val remote = KtorProblemRemote(apiClient(engine))

        remote.delete("problem-1")

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Delete, request.method)
        assertEquals("/api/v1/problems/problem-1", request.url.encodedPath)
    }

    @Test
    fun aMissingProblemMapsToNotFound() = runTest {
        val engine = MockEngine { jsonResponse("""{"code":"not_found"}""", HttpStatusCode.NotFound) }
        val remote = KtorProblemRemote(apiClient(engine))

        val failure = assertFailsWith<ApiFailure> { remote.get("missing") }

        assertEquals(ApiFailureKind.NOT_FOUND, failure.kind)
        assertEquals("not_found", failure.code)
    }
}
