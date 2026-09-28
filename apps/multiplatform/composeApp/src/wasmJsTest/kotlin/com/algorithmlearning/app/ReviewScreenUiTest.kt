package com.algorithmlearning.app

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
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
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ReviewScreenUiTest {
    private val strings = StringCatalog.of(AppLanguage.ENGLISH)

    @Test
    fun browseRendersEmptyThenContent() = runComposeUiTest {
        val reviews = FakeReviewRepository()
        val viewModel = ReviewViewModel(reviews, FakeProblemRepository(), CoroutineScope(UnconfinedTestDispatcher()))
        viewModel.initialize()

        setContent {
            val state by viewModel.state.collectAsState()
            ReviewScreen(state = state, strings = strings, actions = ReviewActions(refreshDue = viewModel::refreshDue))
        }
        onNodeWithTag("review-empty").assertIsDisplayed()
        reviews.dueHandler = { listOf(problemSummary("p1", "Two Sum")) }
        viewModel.refreshDue()
        onNodeWithText("Two Sum").assertIsDisplayed()
    }

    @Test
    fun dueItemNavigatesToDetailWithoutRenderingAReviewStage() = runComposeUiTest {
        var opened: String? = null
        setContent {
            ReviewScreen(
                state = ReviewUiState(due = DueReviewState.Content(listOf(problemSummary("p1", "Two Sum")))),
                strings = strings,
                actions = ReviewActions(openProblem = { opened = it }),
            )
        }
        onNodeWithTag("review-open-p1").performClick()
        assertEquals("p1", opened)
        assertTrue(onAllNodesWithTag("review-stage-action").fetchSemanticsNodes().isEmpty())
        assertTrue(onAllNodesWithTag("review-submit").fetchSemanticsNodes().isEmpty())
    }
}
