package com.algorithmlearning.app

import com.algorithmlearning.app.auth.AuthErrorKind
import com.algorithmlearning.app.problems.ProblemsMessage
import com.algorithmlearning.shared.AppStrings
import com.algorithmlearning.shared.library.ApiFailureKind
import com.algorithmlearning.shared.library.ProblemDifficulty
import com.algorithmlearning.shared.library.ProblemPlatform
import com.algorithmlearning.shared.library.ReviewStatus

/**
 * Presentation-only label resolution. Composables never hard-code a visible
 * string; they ask the catalog through these helpers.
 */
fun ProblemDifficulty.label(strings: AppStrings): String = when (this) {
    ProblemDifficulty.EASY -> strings.difficultyEasy
    ProblemDifficulty.MEDIUM -> strings.difficultyMedium
    ProblemDifficulty.HARD -> strings.difficultyHard
}

fun ProblemPlatform.label(strings: AppStrings): String = when (this) {
    ProblemPlatform.LEETCODE -> strings.platformLeetCode
    ProblemPlatform.HACKER_RANK -> strings.platformHackerRank
    ProblemPlatform.OTHER -> strings.platformOther
}

fun ReviewStatus.label(strings: AppStrings): String = when (this) {
    ReviewStatus.NEVER_REVIEWED -> strings.reviewNeverReviewed
    ReviewStatus.DUE -> strings.reviewDue
    ReviewStatus.SCHEDULED -> strings.reviewScheduled
}

fun AuthErrorKind.label(strings: AppStrings): String = when (this) {
    AuthErrorKind.MISSING_CREDENTIALS -> strings.authMissingCredentialsMessage
    AuthErrorKind.INVALID_CREDENTIALS -> strings.authInvalidCredentialsMessage
    AuthErrorKind.RATE_LIMITED -> strings.authRateLimitedMessage
    AuthErrorKind.NETWORK -> strings.authNetworkErrorMessage
    AuthErrorKind.SERVER -> strings.authServerErrorMessage
    AuthErrorKind.UNEXPECTED -> strings.authUnexpectedErrorMessage
}

fun ApiFailureKind.label(strings: AppStrings): String = when (this) {
    ApiFailureKind.RATE_LIMITED -> strings.authRateLimitedMessage
    ApiFailureKind.NETWORK -> strings.authNetworkErrorMessage
    ApiFailureKind.SERVER -> strings.authServerErrorMessage
    else -> strings.problemOperationFailedMessage
}

fun ProblemsMessage.label(strings: AppStrings): String = when (this) {
    ProblemsMessage.PROBLEM_DELETED -> strings.problemDeletedMessage
    ProblemsMessage.OPERATION_FAILED -> strings.problemOperationFailedMessage
}
