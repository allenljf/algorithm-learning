package com.algorithmlearning.shared.course
interface CourseRepository { suspend fun list(query: CourseQuery = CourseQuery()): CoursePage; suspend fun detail(sourceIdentity: String): CourseLesson }
interface CourseRemote { suspend fun list(query: CourseQuery): CoursePage; suspend fun detail(sourceIdentity: String): CourseLesson }
class RemoteCourseRepository(private val remote: CourseRemote) : CourseRepository { override suspend fun list(query: CourseQuery) = remote.list(query); override suspend fun detail(sourceIdentity: String) = remote.detail(sourceIdentity) }
