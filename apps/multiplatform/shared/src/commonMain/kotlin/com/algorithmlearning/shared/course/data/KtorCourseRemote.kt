package com.algorithmlearning.shared.course.data
import com.algorithmlearning.shared.course.*
import com.algorithmlearning.shared.library.data.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
class KtorCourseRemote(private val api: ApiClient): CourseRemote {
 override suspend fun list(query:CourseQuery):CoursePage=api.decode { api.client.get("${api.root}/courses") { query.category?.let { parameter("category",it) }; query.tag?.let { parameter("tag",it) }; query.keyword?.let { parameter("keyword",it) }; parameter("page",query.page); parameter("pageSize",query.pageSize) }.body<CoursePageDto>().toDomain() }
 override suspend fun detail(sourceIdentity:String):CourseLesson=api.decode { api.client.get("${api.root}/courses/$sourceIdentity").body<CourseLessonDto>().toDomain() }
}
