@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.algorithmlearning.app.acceptance

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import com.algorithmlearning.app.auth.AuthScreen
import com.algorithmlearning.app.auth.AuthViewModel
import com.algorithmlearning.app.dashboard.DashboardScreen
import com.algorithmlearning.app.dashboard.DashboardViewModel
import com.algorithmlearning.app.problems.ProblemsActions
import com.algorithmlearning.app.problems.ProblemsScreen
import com.algorithmlearning.app.problems.ProblemsViewModel
import com.algorithmlearning.app.review.ReviewActions
import com.algorithmlearning.app.review.ReviewScreen
import com.algorithmlearning.app.review.ReviewViewModel
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.StringCatalog
import com.algorithmlearning.shared.auth.AuthSessionHolder
import com.algorithmlearning.shared.library.ProblemDifficulty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Web (Wasm) acceptance journeys that render the real Compose screens through
 * the same stateful [ControlledApiAdapter] used by the cross-platform suite.
 * Each test drives one experience from the UI and asserts on the rendered
 * output, so the delivered Android/Web behavior is exercised end to end.
 */
@OptIn(ExperimentalTestApi::class)
class AcceptanceUiTest {

    private val strings = StringCatalog.of(AppLanguage.ENGLISH)

    @Test
    fun authExperienceSignsInThroughTheForm() = runComposeUiTest {
        val api = ControlledApiAdapter()
        val holder = AuthSessionHolder(api.auth)
        val viewModel = AuthViewModel(holder, CoroutineScope(UnconfinedTestDispatcher()))
        viewModel.toggleMode()

        setContent {
            val state by viewModel.form.collectAsState()
            AuthScreen(
                state = state,
                strings = strings,
                onEmailChange = viewModel::emailChanged,
                onPasswordChange = viewModel::passwordChanged,
                onSubmit = viewModel::submit,
                onToggleMode = viewModel::toggleMode,
            )
        }

        onNodeWithTag("auth-email").performTextInput("ada@example.com")
        onNodeWithTag("auth-password").performTextInput("secret-password")
        onNodeWithTag("auth-submit").performClick()

        assertNotNull(holder.state.value.session)
        assertEquals("ada@example.com", holder.state.value.session?.user?.email)
    }

    @Test
    fun problemExperienceCreatesAndShowsADetail() = runComposeUiTest {
        val api = ControlledApiAdapter()
        val viewModel = ProblemsViewModel(
            api.problems,
            api.tags,
            api.solutions,
            CoroutineScope(UnconfinedTestDispatcher()),
        )
        viewModel.initialize()

        setContent {
            val state by viewModel.state.collectAsState()
            ProblemsScreen(state = state, strings = strings, actions = problemsActions(viewModel))
        }

        onNodeWithTag("problem-add").performClick()
        onNodeWithTag("problem-title").performTextInput("Two Sum")
        onNodeWithTag("problem-save").performClick()

        waitForIdle()
        onNodeWithText("Two Sum").assertIsDisplayed()
        onNodeWithTag("solution-code").performTextInput("return complement")
        onNodeWithTag("solution-save").performClick()
        onNodeWithText("return complement").assertIsDisplayed()
    }

    @Test
    fun scheduledReviewOpensTheLearningDetailWithoutAStageGate() = runComposeUiTest {
        val api = ControlledApiAdapter()
        val problemId = api.seedProblem(
            title = "Two Sum",
            keyInsight = "Use the complement",
            notes = "Hash map approach",
        )
        val viewModel = ReviewViewModel(api.reviews, api.problems, CoroutineScope(UnconfinedTestDispatcher()))
        viewModel.initialize()
        var opened: String? = null

        setContent {
            val state by viewModel.state.collectAsState()
            ReviewScreen(
                state = state,
                strings = strings,
                actions = ReviewActions(refreshDue = viewModel::refreshDue, openProblem = { opened = it }),
            )
        }

        onNodeWithTag("review-open-$problemId").performClick()
        assertEquals(problemId, opened)
        assertTrue(onAllNodesWithTag("review-stage-action").fetchSemanticsNodes().isEmpty())
        assertTrue(onAllNodesWithTag("review-submit").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun dashboardExperienceShowsTheAggregate() = runComposeUiTest {
        val api = ControlledApiAdapter()
        api.seedProblem(title = "Two Sum", difficulty = ProblemDifficulty.EASY)
        api.seedProblem(title = "Median of Two Sorted Arrays", difficulty = ProblemDifficulty.HARD)
        val viewModel = DashboardViewModel(api.dashboard, CoroutineScope(UnconfinedTestDispatcher()))
        viewModel.load()

        setContent {
            val state by viewModel.state.collectAsState()
            DashboardScreen(state = state, strings = strings)
        }

        onNodeWithTag("dashboard-total").assertTextEquals("2")
        onNodeWithTag("dashboard-easy").assertTextEquals("1")
        onNodeWithTag("dashboard-hard").assertTextEquals("1")
        onNodeWithTag("dashboard-due").assertTextEquals("2")
    }

    private fun problemsActions(viewModel: ProblemsViewModel) = ProblemsActions(
        refresh = viewModel::refresh,
        searchChanged = viewModel::searchChanged,
        search = viewModel::search,
        difficultyFilterChanged = viewModel::difficultyFilterChanged,
        platformFilterChanged = viewModel::platformFilterChanged,
        reviewStatusFilterChanged = viewModel::reviewStatusFilterChanged,
        toggleTag = viewModel::toggleTagFilter,
        previousPage = viewModel::previousPage,
        nextPage = viewModel::nextPage,
        openProblem = viewModel::openProblem,
        newProblem = viewModel::newProblem,
        editProblem = viewModel::editProblem,
        cancelEditor = viewModel::cancelEditor,
        editorTitleChanged = viewModel::editorTitleChanged,
        editorPlatformChanged = viewModel::editorPlatformChanged,
        editorDifficultyChanged = viewModel::editorDifficultyChanged,
        editorNotesChanged = viewModel::editorNotesChanged,
        editorTagNameChanged = viewModel::editorTagNameChanged,
        toggleEditorTag = viewModel::toggleEditorTag,
        saveProblem = viewModel::saveProblem,
        createTag = viewModel::createTag,
        requestDeleteProblem = viewModel::requestDeleteProblem,
        cancelDeleteProblem = viewModel::cancelDeleteProblem,
        confirmDeleteProblem = viewModel::confirmDeleteProblem,
        backToList = viewModel::backToList,
        solutionLanguageChanged = viewModel::solutionLanguageChanged,
        solutionCodeChanged = viewModel::solutionCodeChanged,
        solutionExplanationChanged = viewModel::solutionExplanationChanged,
        saveSolution = viewModel::saveSolution,
        deleteSolution = viewModel::deleteSolution,
    )

}
