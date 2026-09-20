package com.algorithmlearning.shared.library.data

import com.algorithmlearning.shared.library.Dashboard
import com.algorithmlearning.shared.library.Page
import com.algorithmlearning.shared.library.ProblemDetail
import com.algorithmlearning.shared.library.ProblemDifficulty
import com.algorithmlearning.shared.library.ProblemPlatform
import com.algorithmlearning.shared.library.ProblemSummary
import com.algorithmlearning.shared.library.ProblemWrite
import com.algorithmlearning.shared.library.Review
import com.algorithmlearning.shared.library.ReviewStatus
import com.algorithmlearning.shared.library.ReviewSummary
import com.algorithmlearning.shared.library.Solution
import com.algorithmlearning.shared.library.SolutionLanguage
import com.algorithmlearning.shared.library.SolutionWrite
import com.algorithmlearning.shared.library.Tag
import kotlin.time.Instant

internal fun TagDto.toDomain(): Tag = Tag(id = id, name = name)

internal fun ReviewSummaryDto.toDomain(): ReviewSummary = ReviewSummary(
    status = ReviewStatus.fromWire(status),
    confidence = confidence,
    lastReviewedAt = lastReviewedAt?.let(Instant::parse),
    nextReviewAt = nextReviewAt?.let(Instant::parse),
    reviewCount = reviewCount,
    intervalDays = intervalDays,
    scheduleExplanationKey = scheduleExplanationKey,
)

internal fun ProblemSummaryDto.toDomain(): ProblemSummary = ProblemSummary(
    id = id,
    title = title,
    platform = ProblemPlatform.fromWire(platform),
    externalProblemId = externalProblemId,
    difficulty = ProblemDifficulty.fromWire(difficulty),
    tags = tags.map(TagDto::toDomain),
    review = review.toDomain(),
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
)

internal fun ProblemDetailDto.toDomain(): ProblemDetail = ProblemDetail(
    summary = ProblemSummary(
        id = id,
        title = title,
        platform = ProblemPlatform.fromWire(platform),
        externalProblemId = externalProblemId,
        difficulty = ProblemDifficulty.fromWire(difficulty),
        tags = tags.map(TagDto::toDomain),
        review = review.toDomain(),
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    ),
    externalUrl = externalUrl,
    description = description,
    notes = notes,
    keyInsight = keyInsight,
    timeComplexity = timeComplexity,
    spaceComplexity = spaceComplexity,
    mistakes = mistakes,
    interviewNotes = interviewNotes,
    solutions = solutions.map(SolutionDto::toDomain),
)

internal fun SolutionDto.toDomain(): Solution = Solution(
    id = id,
    problemId = problemId,
    language = SolutionLanguage.fromWire(language),
    code = code,
    explanation = explanation,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
)

internal fun ReviewDto.toDomain(): Review = Review(
    id = id,
    problemId = problemId,
    confidence = confidence,
    reviewedAt = Instant.parse(reviewedAt),
    nextReviewAt = Instant.parse(nextReviewAt),
    notes = notes,
    policyVersion = policyVersion,
    intervalDays = intervalDays,
    easeFactor = easeFactor,
    repetitions = repetitions,
    scheduleExplanationKey = scheduleExplanationKey,
)

internal fun DashboardDto.toDomain(): Dashboard = Dashboard(
    totalProblems = totalProblems,
    easy = easy,
    medium = medium,
    hard = hard,
    dueReviewCount = dueReviewCount,
)

internal fun <T, R> PageDto<T>.toDomain(map: (T) -> R): Page<R> = Page(
    items = items.map(map),
    page = page,
    pageSize = pageSize,
    totalItems = totalItems,
    totalPages = totalPages,
)

internal fun ProblemWrite.toDto(): ProblemWriteDto = ProblemWriteDto(
    title = title,
    platform = platform.wire,
    difficulty = difficulty.wire,
    externalProblemId = externalProblemId,
    externalUrl = externalUrl,
    description = description,
    notes = notes,
    keyInsight = keyInsight,
    timeComplexity = timeComplexity,
    spaceComplexity = spaceComplexity,
    mistakes = mistakes,
    interviewNotes = interviewNotes,
    tagIds = tagIds,
)

internal fun SolutionWrite.toDto(): SolutionWriteDto = SolutionWriteDto(
    language = language.wire,
    code = code,
    explanation = explanation,
)
