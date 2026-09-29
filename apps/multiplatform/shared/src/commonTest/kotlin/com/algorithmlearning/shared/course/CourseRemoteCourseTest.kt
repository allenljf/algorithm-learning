package com.algorithmlearning.shared.course
import com.algorithmlearning.shared.library.apiClient
import com.algorithmlearning.shared.library.jsonResponse
import com.algorithmlearning.shared.course.data.KtorCourseRemote
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.*
class CourseRemoteCourseTest { @Test fun mapsPublicFilterAndFullDetail()=runTest { var path=""; val remote=KtorCourseRemote(apiClient(MockEngine { request->path=request.url.toString(); jsonResponse("""{"categories":[{"slug":"leetcode75","displayName":"LeetCode 75","sortOrder":1}],"items":[{"sourceIdentity":"x","category":{"slug":"leetcode75","displayName":"LeetCode 75","sortOrder":1},"englishTitle":"Two Sum","tags":["arrays"],"sourceMarkdownPath":"a.md","sourceUrl":"https://x","sourceType":"official","sortOrder":1}],"page":1,"pageSize":20,"totalItems":1}""") })); val page=remote.list(CourseQuery(category="leetcode75",tag="arrays",keyword="two")); assertEquals("Two Sum",page.items.single().englishTitle); assertTrue(path.contains("category=leetcode75")); assertEquals(HttpMethod.Get.value,"GET") } }
