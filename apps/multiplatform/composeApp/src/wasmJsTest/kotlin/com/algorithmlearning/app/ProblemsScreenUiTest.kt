package com.algorithmlearning.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.algorithmlearning.app.problems.ProblemEditorState
import com.algorithmlearning.app.problems.ProblemListState
import com.algorithmlearning.app.problems.ProblemsActions
import com.algorithmlearning.app.problems.ProblemsScreen
import com.algorithmlearning.app.problems.ProblemsUiState
import com.algorithmlearning.app.problems.ProblemsView
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.StringCatalog
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ProblemsScreenUiTest {

    private val strings = StringCatalog.of(AppLanguage.ENGLISH)

    @Test
    fun listRendersLoadingEmptyAndDataStates() = runComposeUiTest {
        val loading = ProblemsUiState(list = ProblemListState.Loading)
        setContent { ProblemsScreen(state = loading, strings = strings) }
        onNodeWithTag("problems-loading").assertIsDisplayed()
    }

    @Test
    fun emptyStateInvitesAddingAProblem() = runComposeUiTest {
        var addClicks = 0
        val state = ProblemsUiState(
            list = ProblemListState.Content(items = emptyList(), page = 1, totalPages = 1, totalItems = 0),
        )
        setContent {
            ProblemsScreen(
                state = state,
                strings = strings,
                actions = ProblemsActions(newProblem = { addClicks++ }),
            )
        }
        onNodeWithText(strings.problemEmptyMessage).assertIsDisplayed()
        onNodeWithTag("problem-add").performClick()
        assertTrue(addClicks == 1)
    }

    @Test
    fun populatedListShowsProblemsAndInvokesOpen() = runComposeUiTest {
        var opened: String? = null
        val state = ProblemsUiState(
            list = ProblemListState.Content(
                items = listOf(problemSummary("p1", "Two Sum")),
                page = 1,
                totalPages = 1,
                totalItems = 1,
            ),
        )
        setContent {
            ProblemsScreen(
                state = state,
                strings = strings,
                actions = ProblemsActions(openProblem = { opened = it }),
            )
        }
        onNodeWithText("Two Sum").assertIsDisplayed()
        onNodeWithText("Two Sum").performClick()
        assertTrue(opened == "p1")
    }

    @Test
    fun editorShowsRequiredTitleErrorFromState() = runComposeUiTest {
        val state = ProblemsUiState(
            view = ProblemsView.EDITOR,
            editor = ProblemEditorState(titleMissing = true),
        )
        setContent { ProblemsScreen(state = state, strings = strings) }
        onNodeWithText(strings.problemNewTitle).assertIsDisplayed()
        onNodeWithText(strings.problemTitleRequiredMessage).assertIsDisplayed()
    }

    @Test
    fun detailShowsNotesAndSolutionEditor() = runComposeUiTest {
        val detail = problemDetail("p1", "Two Sum", notes = "中文題目描述").copy(
            description = "English problem description",
            keyInsight = "中文一句話提示",
            timeComplexity = "O(n)",
            spaceComplexity = "O(n)",
            interviewNotes = "assertEquals(9, solve())",
        )
        val solution = problemSolution(
            "s1", "p1", code = "// 中文註解\nfun solve() = 9", explanation = "中文完整解題思路",
        )
        val state = ProblemsUiState(
            view = ProblemsView.DETAIL,
            detail = com.algorithmlearning.app.problems.ProblemDetailState(detail = detail, solutions = listOf(solution)),
        )
        setContent { ProblemsScreen(state = state, strings = strings) }
        onNodeWithText("English problem description").assertIsDisplayed()
        onNodeWithText("中文題目描述").assertIsDisplayed()
        onNodeWithText("中文一句話提示").assertIsDisplayed()
        onNodeWithText("中文完整解題思路").assertIsDisplayed()
        onNodeWithText("assertEquals(9, solve())").assertIsDisplayed()
        onNodeWithTag("detail-learning-content").assertIsDisplayed()
        onNodeWithTag("problem-delete").assertIsDisplayed()
    }
}
