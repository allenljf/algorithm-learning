@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.algorithmlearning.app.acceptance

import com.algorithmlearning.app.auth.AuthViewModel
import com.algorithmlearning.app.dashboard.DashboardState
import com.algorithmlearning.app.dashboard.DashboardViewModel
import com.algorithmlearning.app.problems.ProblemListState
import com.algorithmlearning.app.problems.ProblemsView
import com.algorithmlearning.app.problems.ProblemsViewModel
import com.algorithmlearning.app.review.DueReviewState
import com.algorithmlearning.app.review.ReviewStage
import com.algorithmlearning.app.review.ReviewViewModel
import com.algorithmlearning.shared.auth.AuthSessionHolder
import com.algorithmlearning.shared.library.ProblemDifficulty
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Cross-platform acceptance journey for the Compose client. It runs from the
 * `commonTest` source set, so the same suite executes on Android
 * (`testDebugUnitTest`) and Web (`wasmJsBrowserTest`).
 *
 * The real presentation state holders drive a single stateful
 * [ControlledApiAdapter], proving the four experiences share one coherent
 * session: register, create/search a problem with a solution and tag, work the
 * staged review, then read the dashboard aggregate the journey produced.
 */
class ComposeAcceptanceTest {

    @Test
    fun theFourExperiencesShareOneControlledSession() = runTest(UnconfinedTestDispatcher()) {
        val api = ControlledApiAdapter()

        // 1. Auth experience: register and hold a session.
        val sessionHolder = AuthSessionHolder(api.auth)
        val auth = AuthViewModel(sessionHolder, backgroundScope)
        auth.toggleMode()
        auth.emailChanged("ada@example.com")
        auth.passwordChanged("secret")
        auth.submit()
        advanceUntilIdle()
        assertEquals("ada@example.com", sessionHolder.state.value.session?.user?.email)

        // 2. Problem management: empty library, create with a tag, add a solution.
        val problems = ProblemsViewModel(api.problems, api.tags, api.solutions, backgroundScope)
        problems.initialize()
        advanceUntilIdle()
        assertTrue((problems.state.value.list as ProblemListState.Content).items.isEmpty())

        problems.newProblem()
        problems.editorTagNameChanged("array")
        problems.createTag()
        advanceUntilIdle()
        val arrayTag = problems.state.value.tags.single()
        assertEquals("array", arrayTag.name)

        problems.toggleEditorTag(arrayTag.id)
        problems.editorTitleChanged("Two Sum")
        problems.editorDifficultyChanged(ProblemDifficulty.HARD)
        problems.editorNotesChanged("Use a hash map")
        problems.saveProblem()
        advanceUntilIdle()

        assertEquals(ProblemsView.DETAIL, problems.state.value.view)
        val savedId = problems.state.value.detail!!.detail.summary.id
        assertEquals("Two Sum", problems.state.value.detail!!.detail.summary.title)
        assertEquals(listOf(arrayTag), problems.state.value.detail!!.detail.summary.tags)

        problems.solutionCodeChanged("return complement")
        problems.saveSolution()
        advanceUntilIdle()
        assertEquals(1, problems.state.value.detail!!.solutions.size)

        // Search and filter reload the narrowed list.
        problems.backToList()
        problems.searchChanged("two")
        problems.search()
        advanceUntilIdle()
        assertEquals(
            listOf("Two Sum"),
            (problems.state.value.list as ProblemListState.Content).items.map { it.title },
        )
        problems.difficultyFilterChanged(ProblemDifficulty.HARD)
        advanceUntilIdle()
        assertEquals(1, (problems.state.value.list as ProblemListState.Content).items.size)

        // 3. Review experience: the new problem is due; reveal in order, submit.
        val review = ReviewViewModel(api.reviews, api.problems, backgroundScope)
        review.initialize()
        advanceUntilIdle()
        assertTrue(
            (review.state.value.due as DueReviewState.Content).problems.any { it.id == savedId },
        )

        review.openProblem(savedId)
        advanceUntilIdle()
        assertEquals(ReviewStage.PROBLEM, review.state.value.session!!.stage)

        review.startThinking()
        assertEquals(ReviewStage.THINK, review.state.value.session!!.stage)
        review.revealHint()
        review.revealApproach()
        review.revealSolution()
        review.rateConfidence()
        assertEquals(ReviewStage.CONFIDENCE, review.state.value.session!!.stage)

        review.confidenceSelected(4)
        review.notesChanged("  recursion plus memo  ")
        review.submitReview()
        advanceUntilIdle()
        assertTrue(review.state.value.session!!.submitted)
        assertEquals(emptyList(), (review.state.value.due as DueReviewState.Content).problems)

        val storedReview = api.reviews.history(savedId).single()
        assertEquals(4, storedReview.confidence)
        assertEquals("recursion plus memo", storedReview.notes)

        // 4. Dashboard: the aggregate reflects the journey that just ran.
        val dashboard = DashboardViewModel(api.dashboard, backgroundScope)
        dashboard.load()
        advanceUntilIdle()
        val aggregate = (dashboard.state.value as DashboardState.Content).dashboard
        assertEquals(1L, aggregate.totalProblems)
        assertEquals(1L, aggregate.hard)
        assertEquals(0L, aggregate.dueReviewCount)

        // The session can be ended, returning the client to the auth gate.
        auth.logout()
        advanceUntilIdle()
        assertNull(sessionHolder.state.value.session)
        assertNull(api.auth.restore())
    }
}
