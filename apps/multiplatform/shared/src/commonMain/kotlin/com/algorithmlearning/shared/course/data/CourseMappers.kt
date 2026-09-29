package com.algorithmlearning.shared.course.data
import com.algorithmlearning.shared.course.*
internal fun CourseCategoryDto.toDomain()=CourseCategory(slug,displayName,sortOrder)
internal fun CourseLessonDto.toSummary()=CourseLessonSummary(sourceIdentity,category.toDomain(),englishTitle,tags,sourceMarkdownPath,sourceUrl,sourceType,sortOrder)
internal fun CourseLessonDto.toDomain()=CourseLesson(toSummary(),requireNotNull(detail))
internal fun CoursePageDto.toDomain()=CoursePage(categories.map { it.toDomain() },items.map { it.toSummary() },page,pageSize,totalItems)
