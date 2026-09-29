package com.algorithmlearning.shared.course

data class CourseCategory(val slug: String, val displayName: String, val sortOrder: Int)
data class CourseLessonSummary(val sourceIdentity: String, val category: CourseCategory, val englishTitle: String, val tags: List<String>, val sourceMarkdownPath: String, val sourceUrl: String, val sourceType: String, val sortOrder: Int)
data class CourseLesson(val summary: CourseLessonSummary, val detail: String)
data class CoursePage(val categories: List<CourseCategory>, val items: List<CourseLessonSummary>, val page: Int, val pageSize: Int, val totalItems: Long)
data class CourseQuery(val category: String? = null, val tag: String? = null, val keyword: String? = null, val page: Int = 1, val pageSize: Int = 20)
