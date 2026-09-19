package com.algorithmlearning.shared.library.data

import kotlinx.serialization.Serializable

@Serializable
internal data class TagDto(
    val id: String,
    val name: String,
)

@Serializable
internal data class ReviewSummaryDto(
    val status: String,
    val confidence: Int? = null,
    val lastReviewedAt: String? = null,
    val nextReviewAt: String? = null,
    val reviewCount: Int,
)

@Serializable
internal data class ProblemSummaryDto(
    val id: String,
    val title: String,
    val platform: String,
    val externalProblemId: String? = null,
    val difficulty: String,
    val tags: List<TagDto> = emptyList(),
    val review: ReviewSummaryDto,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
internal data class ProblemDetailDto(
    val id: String,
    val title: String,
    val platform: String,
    val externalProblemId: String? = null,
    val externalUrl: String? = null,
    val difficulty: String,
    val tags: List<TagDto> = emptyList(),
    val review: ReviewSummaryDto,
    val description: String? = null,
    val notes: String? = null,
    val keyInsight: String? = null,
    val timeComplexity: String? = null,
    val spaceComplexity: String? = null,
    val mistakes: String? = null,
    val interviewNotes: String? = null,
    val solutions: List<SolutionDto> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
internal data class SolutionDto(
    val id: String,
    val problemId: String,
    val language: String,
    val code: String,
    val explanation: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
internal data class PageDto<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
)

@Serializable
internal data class ProblemWriteDto(
    val title: String,
    val platform: String,
    val difficulty: String,
    val externalProblemId: String? = null,
    val externalUrl: String? = null,
    val description: String? = null,
    val notes: String? = null,
    val keyInsight: String? = null,
    val timeComplexity: String? = null,
    val spaceComplexity: String? = null,
    val mistakes: String? = null,
    val interviewNotes: String? = null,
    val tagIds: List<String> = emptyList(),
)

@Serializable
internal data class SolutionWriteDto(
    val language: String,
    val code: String,
    val explanation: String? = null,
)

@Serializable
internal data class TagCreateDto(
    val name: String,
)

@Serializable
internal data class ReviewDto(
    val id: String,
    val problemId: String,
    val confidence: Int,
    val reviewedAt: String,
    val nextReviewAt: String,
    val notes: String? = null,
    val policyVersion: String,
)

@Serializable
internal data class ReviewWriteDto(
    val problemId: String,
    val confidence: Int,
    val notes: String? = null,
)

@Serializable
internal data class DashboardDto(
    val totalProblems: Long,
    val easy: Long,
    val medium: Long,
    val hard: Long,
    val dueReviewCount: Long,
)

@Serializable
internal data class ProblemDto(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val code: String? = null,
    val detail: String? = null,
    val instance: String? = null,
    val requestId: String? = null,
    val fieldErrors: Map<String, List<String>>? = null,
)
