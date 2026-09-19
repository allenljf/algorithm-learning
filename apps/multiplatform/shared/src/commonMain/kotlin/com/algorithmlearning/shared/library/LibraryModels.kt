package com.algorithmlearning.shared.library

import kotlin.time.Instant

/** Platform wire values are fixed by the API contract. */
enum class ProblemPlatform(val wire: String) {
    LEETCODE("leetcode"),
    HACKER_RANK("hacker_rank"),
    OTHER("other"),
    ;

    companion object {
        fun fromWire(value: String): ProblemPlatform =
            entries.firstOrNull { it.wire == value }
                ?: throw IllegalArgumentException("Unknown platform: $value")
    }
}

enum class ProblemDifficulty(val wire: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard"),
    ;

    companion object {
        fun fromWire(value: String): ProblemDifficulty =
            entries.firstOrNull { it.wire == value }
                ?: throw IllegalArgumentException("Unknown difficulty: $value")
    }
}

enum class ReviewStatus(val wire: String) {
    NEVER_REVIEWED("neverReviewed"),
    DUE("due"),
    SCHEDULED("scheduled"),
    ;

    companion object {
        fun fromWire(value: String): ReviewStatus =
            entries.firstOrNull { it.wire == value }
                ?: throw IllegalArgumentException("Unknown review status: $value")
    }
}

enum class ProblemSort(val wire: String) {
    UPDATED_DESC("updatedDesc"),
    CREATED_DESC("createdDesc"),
    TITLE_ASC("titleAsc"),
}

enum class SolutionLanguage(val wire: String) {
    KOTLIN("kotlin"),
    JAVA("java"),
    PYTHON("python"),
    DART("dart"),
    ;

    companion object {
        fun fromWire(value: String): SolutionLanguage =
            entries.firstOrNull { it.wire == value }
                ?: throw IllegalArgumentException("Unknown language: $value")
    }
}

/** A page of remote items, mapped at the data-layer boundary. */
data class Page<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
)

data class Tag(
    val id: String,
    val name: String,
)

data class ReviewSummary(
    val status: ReviewStatus,
    val confidence: Int?,
    val lastReviewedAt: Instant?,
    val nextReviewAt: Instant?,
    val reviewCount: Int,
)

data class ProblemSummary(
    val id: String,
    val title: String,
    val platform: ProblemPlatform,
    val externalProblemId: String?,
    val difficulty: ProblemDifficulty,
    val tags: List<Tag>,
    val review: ReviewSummary,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class ProblemDetail(
    val summary: ProblemSummary,
    val externalUrl: String?,
    val description: String?,
    val notes: String?,
    val keyInsight: String?,
    val timeComplexity: String?,
    val spaceComplexity: String?,
    val mistakes: String?,
    val interviewNotes: String?,
    val solutions: List<Solution>,
)

data class ProblemWrite(
    val title: String,
    val platform: ProblemPlatform,
    val difficulty: ProblemDifficulty,
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

data class ProblemQuery(
    val query: String? = null,
    val difficulty: ProblemDifficulty? = null,
    val platform: ProblemPlatform? = null,
    val tagIds: List<String> = emptyList(),
    val reviewStatus: ReviewStatus? = null,
    val sort: ProblemSort = ProblemSort.UPDATED_DESC,
    val page: Int = 1,
    val pageSize: Int = 20,
)

data class Solution(
    val id: String,
    val problemId: String,
    val language: SolutionLanguage,
    val code: String,
    val explanation: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class SolutionWrite(
    val language: SolutionLanguage,
    val code: String,
    val explanation: String? = null,
)

data class Review(
    val id: String,
    val problemId: String,
    val confidence: Int,
    val reviewedAt: Instant,
    val nextReviewAt: Instant,
    val notes: String?,
    val policyVersion: String,
)

data class Dashboard(
    val totalProblems: Long,
    val easy: Long,
    val medium: Long,
    val hard: Long,
    val dueReviewCount: Long,
)
