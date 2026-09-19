package com.algorithmlearning.shared.library

import com.algorithmlearning.shared.library.data.KtorDashboardRemote
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KtorDashboardRemoteTest {

    @Test
    fun getReadsTheDashboardCounts() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            jsonResponse(dashboardJson)
        }
        val remote = KtorDashboardRemote(apiClient(engine, FakeAuthRepository("token-1")))

        val dashboard = remote.get()

        val request = assertNotNull(captured)
        assertEquals("/api/v1/dashboard", request.url.encodedPath)
        assertEquals("Bearer token-1", request.headers[HttpHeaders.Authorization])
        assertEquals(4, dashboard.totalProblems)
        assertEquals(1, dashboard.dueReviewCount)
    }
}
