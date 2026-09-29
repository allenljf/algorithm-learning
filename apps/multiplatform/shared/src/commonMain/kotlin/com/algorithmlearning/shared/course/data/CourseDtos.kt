package com.algorithmlearning.shared.course.data
import kotlinx.serialization.Serializable
@Serializable internal data class CourseCategoryDto(val slug:String,val displayName:String,val sortOrder:Int)
@Serializable internal data class CourseLessonDto(val sourceIdentity:String,val category:CourseCategoryDto,val englishTitle:String,val tags:List<String>,val detail:String?=null,val sourceMarkdownPath:String,val sourceUrl:String,val sourceType:String,val sortOrder:Int)
@Serializable internal data class CoursePageDto(val categories:List<CourseCategoryDto>,val items:List<CourseLessonDto>,val page:Int,val pageSize:Int,val totalItems:Long)
