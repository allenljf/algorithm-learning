package com.algorithmlearning.app

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.algorithmlearning.app.review.DueReviewState
import com.algorithmlearning.app.review.ReviewActions
import com.algorithmlearning.app.review.ReviewScreen
import com.algorithmlearning.app.review.ReviewUiState
import com.algorithmlearning.app.review.ReviewViewModel
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.StringCatalog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ReviewScreenUiTest {

    private val strings = StringCatalog.of(AppLanguage.ENGLISH)

    private fun actions(viewModel: ReviewViewModel) = ReviewActions(
        refreshDue = viewModel::refreshDue,
        openProblem = viewModel::openProblem,
        exitReview = viewModel::exitReview,
        startThinking = viewModel::startThinking,
        revealHint = viewModel::revealHint,
        revealApproach = viewModel::revealApproach,
        revealSolution = viewModel::revealSolution,
        rateConfidence = viewModel::rateConfidence,
        selectSolution = viewModel::selectSolution,
        confidenceSelected = viewModel::confidenceSelected,
        notesChanged = viewModel::notesChanged,
        submitReview = viewModel::submitReview,
    )

    @Test
    fun browseRendersEmptyThenContent() = runComposeUiTest {
        val reviews = FakeReviewRepository()
        val viewModel = ReviewViewModel(reviews, FakeProblemRepository(), CoroutineScope(UnconfinedTestDispatcher()))
        viewModel.initialize()

        setContent {
            val state by viewModel.state.collectAsState()
            ReviewScreen(state = state, strings = strings, actions = actions(viewModel))
        }
        onNodeWithTag("review-empty").assertIsDisplayed()

        reviews.dueHandler = { listOf(problemSummary("p1", "Two Sum")) }
        viewModel.refreshDue()
        onNodeWithText("Two Sum").assertIsDisplayed()
    }

    @Test
    fun reviewModeEnforcesOrderedRevealAndConfidenceGating() = runComposeUiTest {
        val problems = FakeProblemRepository().apply {
            getHandler = { id ->
                problemDetail(
                    id = id,
                    title = "Two Sum",
                    keyInsight = "Use the complement",
                    notes = "Hash map approach",
                    solutions = listOf(problemSolution("s1", id, code = "return map[n]")),
                )
            }
        }
        val reviews = FakeReviewRepository()
        val viewModel = ReviewViewModel(reviews, problems, CoroutineScope(UnconfinedTestDispatcher()))
        viewModel.openProblem("p1")

        setContent {
            val state by viewModel.state.collectAsState()
            ReviewScreen(state = state, strings = strings, actions = actions(viewModel))
        }

        onNodeWithText(strings.reviewStartThinkingAction).assertIsDisplayed()
        onNodeWithText(strings.reviewRevealHintAction).assertDoesNotExist()
        onNodeWithText(strings.reviewRateConfidenceAction).assertDoesNotExist()

        onNodeWithTag("review-stage-action").performClick()
        onNodeWithText(strings.reviewRevealHintAction).assertIsDisplayed()
        onNodeWithText("Use the complement").assertDoesNotExist()

        onNodeWithTag("review-stage-action").performClick()
        onNodeWithText(strings.reviewRevealApproachAction).assertIsDisplayed()
        onNodeWithText("Use the complement").assertIsDisplayed()

        onNodeWithTag("review-stage-action").performClick()
        onNodeWithText(strings.reviewRevealSolutionAction).assertIsDisplayed()
        onNodeWithText("Hash map approach").assertIsDisplayed()

        onNodeWithTag("review-stage-action").performClick()
        onNodeWithText(strings.reviewRateConfidenceAction).assertIsDisplayed()
        onNodeWithText("return map[n]").assertIsDisplayed()

        onNodeWithTag("review-stage-action").performClick()
        onNodeWithTag("review-confidence-3").assertIsDisplayed()
        onNodeWithTag("review-submit").assertIsNotEnabled()

        onNodeWithTag("review-confidence-3").performClick()
        onNodeWithTag("review-submit").assertIsEnabled()
        onNodeWithTag("review-submit").performClick()

        onNodeWithTag("review-submitted").assertIsDisplayed()
        onNodeWithTag("schedule-context").assertIsDisplayed()
        onNodeWithText(strings.reviewScheduleTitle).assertIsDisplayed()
        assertEquals(1, reviews.submitted.size)
        assertEquals(3, reviews.submitted.single().second)
    }

    @Test
    fun wideLayoutShowsBrowseAndSelectPromptTogether() = runComposeUiTest {
        val state = ReviewUiState(
            due = DueReviewState.Content(listOf(problemSummary("p1", "Two Sum"))),
        )
        setContent { ReviewScreen(state = state, strings = strings, twoPane = true) }
        onNodeWithText("Two Sum").assertIsDisplayed()
        onNodeWithTag("review-select-prompt").assertIsDisplayed()
    }

    @Test
    fun narrowLayoutShowsOnlyTheBrowseList() = runComposeUiTest {
        val state = ReviewUiState(
            due = DueReviewState.Content(listOf(problemSummary("p1", "Two Sum"))),
        )
        setContent { ReviewScreen(state = state, strings = strings, twoPane = false) }
        onNodeWithText("Two Sum").assertIsDisplayed()
        onNodeWithTag("review-select-prompt").assertDoesNotExist()
    }
}
