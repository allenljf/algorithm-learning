@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.algorithmlearning.app.review

import com.algorithmlearning.app.FakeProblemRepository
import com.algorithmlearning.app.FakeReviewRepository
import com.algorithmlearning.app.problemDetail
import com.algorithmlearning.app.problemSolution
import com.algorithmlearning.app.problemSummary
import com.algorithmlearning.shared.library.ApiFailure
import com.algorithmlearning.shared.library.ApiFailureKind
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReviewViewModelTest {

    private val twoSum = problemSummary("p1", "Two Sum")

    @Test
    fun dueListDistinguishesFailureFromEmptyAndRecovers() = runTest(UnconfinedTestDispatcher()) {
        val reviews = FakeReviewRepository().apply {
            dueHandler = { throw ApiFailure(ApiFailureKind.NETWORK) }
        }
        val viewModel = ReviewViewModel(reviews, FakeProblemRepository(), backgroundScope)

        viewModel.initialize()
        advanceUntilIdle()
        assertEquals(DueReviewState.Failed(ApiFailureKind.NETWORK), viewModel.state.value.due)

        reviews.dueHandler = { listOf(twoSum) }
        viewModel.refreshDue()
        advanceUntilIdle()

        val content = viewModel.state.value.due as DueReviewState.Content
        assertEquals(listOf("Two Sum"), content.problems.map { it.title })
    }

    @Test
    fun openingAProblemStartsAtTheProblemStageWithTheFirstSolution() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            getHandler = { id ->
                problemDetail(
                    id = id,
                    title = "Two Sum",
                    keyInsight = "Use the complement",
                    notes = "Hash map",
                    solutions = listOf(problemSolution("s1", id), problemSolution("s2", id)),
                )
            }
        }
        val viewModel = ReviewViewModel(FakeReviewRepository(), problems, backgroundScope)

        viewModel.openProblem("p1")
        advanceUntilIdle()

        val session = viewModel.state.value.session!!
        assertEquals(ReviewStage.PROBLEM, session.stage)
        assertEquals("s1", session.selectedSolutionId)
    }

    @Test
    fun disclosureOrderCannotBeSkipped() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            getHandler = { id -> problemDetail(id, "Two Sum") }
        }
        val viewModel = ReviewViewModel(FakeReviewRepository(), problems, backgroundScope)
        viewModel.openProblem("p1")
        advanceUntilIdle()

        viewModel.revealHint()
        assertEquals(ReviewStage.PROBLEM, stage(viewModel))
        viewModel.rateConfidence()
        assertEquals(ReviewStage.PROBLEM, stage(viewModel))

        viewModel.startThinking()
        assertEquals(ReviewStage.THINK, stage(viewModel))
        viewModel.startThinking()
        assertEquals(ReviewStage.THINK, stage(viewModel))

        viewModel.revealHint()
        assertEquals(ReviewStage.HINT, stage(viewModel))
        viewModel.revealApproach()
        assertEquals(ReviewStage.APPROACH, stage(viewModel))
        viewModel.revealSolution()
        assertEquals(ReviewStage.SOLUTION, stage(viewModel))
        viewModel.rateConfidence()
        assertEquals(ReviewStage.CONFIDENCE, stage(viewModel))
    }

    @Test
    fun confidenceIsBoundedAndRequiredBeforeSubmission() = runTest(UnconfinedTestDispatcher()) {
        val reviews = FakeReviewRepository()
        val problems = FakeProblemRepository().apply {
            getHandler = { id -> problemDetail(id, "Two Sum") }
        }
        val viewModel = ReviewViewModel(reviews, problems, backgroundScope)
        viewModel.openProblem("p1")
        advanceUntilIdle()

        viewModel.submitReview()
        advanceUntilIdle()
        assertTrue(reviews.submitted.isEmpty())

        viewModel.startThinking()
        viewModel.revealHint()
        viewModel.revealApproach()
        viewModel.revealSolution()
        viewModel.rateConfidence()

        viewModel.confidenceSelected(5)
        assertNull(viewModel.state.value.session!!.confidence)

        viewModel.submitReview()
        advanceUntilIdle()
        assertTrue(reviews.submitted.isEmpty())

        viewModel.confidenceSelected(3)
        viewModel.submitReview()
        advanceUntilIdle()

        assertEquals(listOf<Triple<String, Int, String?>>(Triple("p1", 3, null)), reviews.submitted)
        assertTrue(viewModel.state.value.session!!.submitted)
    }

    @Test
    fun aReviewNoteIsTrimmedAndSubmitted() = runTest(UnconfinedTestDispatcher()) {
        val reviews = FakeReviewRepository()
        val problems = FakeProblemRepository().apply {
            getHandler = { id -> problemDetail(id, "Two Sum") }
        }
        val viewModel = ReviewViewModel(reviews, problems, backgroundScope)
        viewModel.openProblem("p1")
        advanceUntilIdle()
        viewModel.startThinking()
        viewModel.revealHint()
        viewModel.revealApproach()
        viewModel.revealSolution()
        viewModel.rateConfidence()
        viewModel.confidenceSelected(4)
        viewModel.notesChanged("  remembered the trick  ")
        viewModel.submitReview()
        advanceUntilIdle()

        assertEquals(
            listOf<Triple<String, Int, String?>>(Triple("p1", 4, "remembered the trick")),
            reviews.submitted,
        )
    }

    @Test
    fun selectingASolutionUpdatesTheSession() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            getHandler = { id ->
                problemDetail(
                    id = id,
                    title = "Two Sum",
                    solutions = listOf(problemSolution("s1", id), problemSolution("s2", id)),
                )
            }
        }
        val viewModel = ReviewViewModel(FakeReviewRepository(), problems, backgroundScope)
        viewModel.openProblem("p1")
        advanceUntilIdle()

        viewModel.selectSolution("s2")
        assertEquals("s2", viewModel.state.value.session!!.selectedSolutionId)
    }

    @Test
    fun exitingReviewReturnsToTheBrowseList() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            getHandler = { id -> problemDetail(id, "Two Sum") }
        }
        val viewModel = ReviewViewModel(FakeReviewRepository(), problems, backgroundScope)
        viewModel.openProblem("p1")
        advanceUntilIdle()

        viewModel.exitReview()
        assertNull(viewModel.state.value.session)
    }

    @Test
    fun aFailedDetailLoadSurfacesASessionError() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            getHandler = { throw ApiFailure(ApiFailureKind.NOT_FOUND) }
        }
        val viewModel = ReviewViewModel(FakeReviewRepository(), problems, backgroundScope)

        viewModel.openProblem("missing")
        advanceUntilIdle()

        assertNull(viewModel.state.value.session)
        assertEquals(ApiFailureKind.NOT_FOUND, viewModel.state.value.sessionError)
    }

    @Test
    fun aFailedSubmissionKeepsTheSessionOpen() = runTest(UnconfinedTestDispatcher()) {
        val reviews = FakeReviewRepository().apply {
            submitHandler = { _, _, _ -> throw ApiFailure(ApiFailureKind.SERVER) }
        }
        val problems = FakeProblemRepository().apply {
            getHandler = { id -> problemDetail(id, "Two Sum") }
        }
        val viewModel = ReviewViewModel(reviews, problems, backgroundScope)
        viewModel.openProblem("p1")
        advanceUntilIdle()
        viewModel.startThinking()
        viewModel.revealHint()
        viewModel.revealApproach()
        viewModel.revealSolution()
        viewModel.rateConfidence()
        viewModel.confidenceSelected(2)
        viewModel.submitReview()
        advanceUntilIdle()

        val session = viewModel.state.value.session!!
        assertFalse(session.submitted)
        assertFalse(session.submitting)
        assertEquals(ApiFailureKind.SERVER, session.error)
    }

    private fun stage(viewModel: ReviewViewModel): ReviewStage =
        viewModel.state.value.session?.stage ?: error("no active session")
}
