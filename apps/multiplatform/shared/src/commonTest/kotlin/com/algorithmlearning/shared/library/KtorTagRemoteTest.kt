package com.algorithmlearning.shared.library

import com.algorithmlearning.shared.library.data.KtorTagRemote
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

class KtorTagRemoteTest {

    @Test
    fun listFiltersByQueryAndMapsTags() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse("""[$tagJson]""")
        }
        val remote = KtorTagRemote(apiClient(engine, FakeAuthRepository("token-1")))

        val tags = remote.list("arr")

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Get, request.method)
        assertEquals("/api/v1/tags", request.url.encodedPath)
        assertEquals("arr", request.url.parameters["q"])
        assertEquals(listOf(testTag()), tags)
    }

    @Test
    fun createPostsTheNameAndMapsTheTag() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(tagJson, HttpStatusCode.Created)
        }
        val remote = KtorTagRemote(apiClient(engine))

        val tag = remote.create("Arrays")

        val request = assertNotNull(captured)
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("/api/v1/tags", request.url.encodedPath)
        assertContains((request.body as TextContent).text, "\"name\":\"Arrays\"")
        assertEquals(testTag(), tag)
    }
}
