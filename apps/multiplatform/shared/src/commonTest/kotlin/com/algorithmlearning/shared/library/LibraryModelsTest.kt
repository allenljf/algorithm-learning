package com.algorithmlearning.shared.library

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class LibraryModelsTest {

    @Test
    fun platformMapsItsWireValues() {
        assertEquals("hacker_rank", ProblemPlatform.HACKER_RANK.wire)
        assertEquals(ProblemPlatform.LEETCODE, ProblemPlatform.fromWire("leetcode"))
        assertEquals(ProblemPlatform.HACKER_RANK, ProblemPlatform.fromWire("hacker_rank"))
        assertEquals(ProblemPlatform.OTHER, ProblemPlatform.fromWire("other"))
        assertFailsWith<IllegalArgumentException> { ProblemPlatform.fromWire("hackerRank") }
    }

    @Test
    fun difficultyAndReviewStatusMapTheirWireValues() {
        assertEquals(ProblemDifficulty.MEDIUM, ProblemDifficulty.fromWire("medium"))
        assertEquals(ReviewStatus.NEVER_REVIEWED, ReviewStatus.fromWire("neverReviewed"))
        assertEquals(ReviewStatus.SCHEDULED, ReviewStatus.fromWire("scheduled"))
        assertFailsWith<IllegalArgumentException> { ReviewStatus.fromWire("later") }
    }

    @Test
    fun solutionLanguageMapsItsWireValues() {
        assertEquals(SolutionLanguage.PYTHON, SolutionLanguage.fromWire("python"))
        assertFailsWith<IllegalArgumentException> { SolutionLanguage.fromWire("ruby") }
    }

    @Test
    fun valuesAreImmutable() {
        val summary = testSummary()
        assertEquals(summary, summary.copy())
        assertNotEquals(summary, summary.copy(title = "Three Sum"))
    }
}
