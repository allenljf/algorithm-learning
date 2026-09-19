package com.algorithmlearning.shared.library

import kotlin.time.Instant

internal val testInstant: Instant = Instant.parse("2026-01-01T00:00:00Z")

internal fun testTag(id: String = "tag-1", name: String = "Arrays"): Tag = Tag(id = id, name = name)

internal fun testReviewSummary(
    status: ReviewStatus = ReviewStatus.NEVER_REVIEWED,
    confidence: Int? = null,
    reviewCount: Int = 0,
): ReviewSummary = ReviewSummary(
    status = status,
    confidence = confidence,
    lastReviewedAt = if (confidence == null) null else testInstant,
    nextReviewAt = testInstant,
    reviewCount = reviewCount,
)

internal fun testSummary(id: String = "problem-1"): ProblemSummary = ProblemSummary(
    id = id,
    title = "Two Sum",
    platform = ProblemPlatform.LEETCODE,
    externalProblemId = "1",
    difficulty = ProblemDifficulty.EASY,
    tags = listOf(testTag()),
    review = testReviewSummary(),
    createdAt = testInstant,
    updatedAt = testInstant,
)

internal fun testSolution(id: String = "solution-1"): Solution = Solution(
    id = id,
    problemId = "problem-1",
    language = SolutionLanguage.KOTLIN,
    code = "fun twoSum() = Unit",
    explanation = "Map complements.",
    createdAt = testInstant,
    updatedAt = testInstant,
)

internal fun testDetail(): ProblemDetail = ProblemDetail(
    summary = testSummary(),
    externalUrl = "https://leetcode.com/problems/two-sum",
    description = "Find two numbers.",
    notes = "Use a hash map.",
    keyInsight = "Complement lookup.",
    timeComplexity = "O(n)",
    spaceComplexity = "O(n)",
    mistakes = "Forgetting duplicates.",
    interviewNotes = "Clarify constraints.",
    solutions = listOf(testSolution()),
)

internal fun testReview(problemId: String = "problem-1"): Review = Review(
    id = "review-1",
    problemId = problemId,
    confidence = 3,
    reviewedAt = testInstant,
    nextReviewAt = testInstant,
    notes = "Solid.",
    policyVersion = "mvp-1",
)

internal fun testDashboard(): Dashboard = Dashboard(
    totalProblems = 4,
    easy = 2,
    medium = 1,
    hard = 1,
    dueReviewCount = 1,
)
