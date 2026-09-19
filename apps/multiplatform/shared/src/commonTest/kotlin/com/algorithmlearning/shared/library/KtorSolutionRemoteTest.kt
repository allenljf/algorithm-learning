package com.algorithmlearning.shared.library

import com.algorithmlearning.shared.library.data.KtorSolutionRemote
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

class KtorSolutionRemoteTest {

    @Test
    fun listReadsProblemScopedSolutions() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse("""[$solutionJson]""")
        }
        val remote = KtorSolutionRemote(apiClient(engine, FakeAuthRepository("token-1")))

        val solutions = remote.list("problem-1")

        val request = assertNotNull(captured)
        assertEquals("/api/v1/problems/problem-1/solutions", request.url.encodedPath)
        assertEquals(SolutionLanguage.KOTLIN, solutions.single().language)
    }

    @Test
    fun createPostsToTheProblemScopedRoute() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(solutionJson, HttpStatusCode.Created)
        }
        val remote = KtorSolutionRemote(apiClient(engine, FakeAuthRepository("token-1")))

        remote.create("problem-1", SolutionWrite(SolutionLanguage.PYTHON, "print(1)"))

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("/api/v1/problems/problem-1/solutions", request.url.encodedPath)
        assertContains((request.body as TextContent).text, "\"language\":\"python\"")
    }

    @Test
    fun replacePutsToTheSolutionRoute() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(solutionJson)
        }
        val remote = KtorSolutionRemote(apiClient(engine))

        remote.replace("solution-1", SolutionWrite(SolutionLanguage.JAVA, "void x(){}"))

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Put, request.method)
        assertEquals("/api/v1/solutions/solution-1", request.url.encodedPath)
        assertContains((request.body as TextContent).text, "\"language\":\"java\"")
    }

    @Test
    fun deleteSendsADeleteToTheSolutionRoute() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse("", HttpStatusCode.NoContent)
        }
        val remote = KtorSolutionRemote(apiClient(engine))

        remote.delete("solution-1")

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Delete, request.method)
        assertEquals("/api/v1/solutions/solution-1", request.url.encodedPath)
    }
}
