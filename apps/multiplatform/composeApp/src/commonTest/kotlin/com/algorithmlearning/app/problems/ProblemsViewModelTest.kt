@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.algorithmlearning.app.problems

import com.algorithmlearning.app.FakeProblemRepository
import com.algorithmlearning.app.FakeSolutionRepository
import com.algorithmlearning.app.FakeTagRepository
import com.algorithmlearning.app.problemDetail
import com.algorithmlearning.app.problemSolution
import com.algorithmlearning.app.problemSummary
import com.algorithmlearning.shared.library.ApiFailure
import com.algorithmlearning.shared.library.ApiFailureKind
import com.algorithmlearning.shared.library.Page
import com.algorithmlearning.shared.library.ProblemDifficulty
import com.algorithmlearning.shared.library.Tag
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProblemsViewModelTest {

    private val twoSum = problemSummary("p1", "Two Sum")

    @Test
    fun initializeLoadsTheListAndTags() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            listHandler = { query -> Page(listOf(twoSum), query.page, query.pageSize, 1, 1) }
        }
        val tags = FakeTagRepository().apply { listResult = listOf(Tag("t1", "array")) }
        val viewModel = ProblemsViewModel(problems, tags, FakeSolutionRepository(), backgroundScope)

        viewModel.initialize()
        advanceUntilIdle()

        val content = viewModel.state.value.list as ProblemListState.Content
        assertEquals(listOf("Two Sum"), content.items.map { it.title })
        assertEquals(listOf(Tag("t1", "array")), viewModel.state.value.tags)
    }

    @Test
    fun listFailureIsDistinctFromEmptyAndRetryRecovers() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            listHandler = { throw ApiFailure(ApiFailureKind.NETWORK) }
        }
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), FakeSolutionRepository(), backgroundScope)

        viewModel.initialize()
        advanceUntilIdle()
        assertEquals(ProblemListState.Failed(ApiFailureKind.NETWORK), viewModel.state.value.list)

        problems.listHandler = { query -> Page(listOf(twoSum), query.page, query.pageSize, 1, 1) }
        viewModel.refresh()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.list is ProblemListState.Content)
    }

    @Test
    fun searchAndFiltersReloadWithAResetPage() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository()
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), FakeSolutionRepository(), backgroundScope)
        viewModel.initialize()
        advanceUntilIdle()
        problems.queries.clear()

        viewModel.searchChanged("two sum")
        viewModel.search()
        advanceUntilIdle()
        assertEquals("two sum", problems.queries.last().query)

        viewModel.difficultyFilterChanged(ProblemDifficulty.HARD)
        advanceUntilIdle()
        assertEquals(ProblemDifficulty.HARD, problems.queries.last().difficulty)
        assertEquals(1, problems.queries.last().page)

        viewModel.toggleTagFilter("t1")
        advanceUntilIdle()
        assertTrue(problems.queries.last().tagIds.contains("t1"))

        viewModel.toggleTagFilter("t1")
        advanceUntilIdle()
        assertFalse(problems.queries.last().tagIds.contains("t1"))
    }

    @Test
    fun paginationStaysWithinBounds() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            listHandler = { query -> Page(listOf(twoSum), query.page, query.pageSize, 40, 2) }
        }
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), FakeSolutionRepository(), backgroundScope)
        viewModel.initialize()
        advanceUntilIdle()

        viewModel.nextPage()
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.query.page)

        viewModel.nextPage()
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.query.page)

        viewModel.previousPage()
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.query.page)

        viewModel.previousPage()
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.query.page)
    }

    @Test
    fun blankTitleIsRejectedBeforeCallingTheRepository() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository()
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), FakeSolutionRepository(), backgroundScope)

        viewModel.newProblem()
        assertEquals(ProblemsView.EDITOR, viewModel.state.value.view)

        viewModel.saveProblem()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.editor!!.titleMissing)
        assertTrue(problems.created.isEmpty())
    }

    @Test
    fun serverFieldErrorsRetainTheDraft() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            createHandler = {
                throw ApiFailure(
                    kind = ApiFailureKind.VALIDATION,
                    statusCode = 400,
                    fieldErrors = mapOf("title" to listOf("must be unique")),
                )
            }
        }
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), FakeSolutionRepository(), backgroundScope)

        viewModel.newProblem()
        viewModel.editorTitleChanged("Two Sum")
        viewModel.editorNotesChanged("Use a hash map")
        viewModel.saveProblem()
        advanceUntilIdle()

        val editor = viewModel.state.value.editor!!
        assertEquals("Two Sum", editor.title)
        assertEquals("Use a hash map", editor.notes)
        assertEquals(listOf("must be unique"), editor.fieldErrors["title"])
        assertFalse(editor.saving)
    }

    @Test
    fun savingCreatesAndOpensTheDetail() = runTest(UnconfinedTestDispatcher()) {
        val created = problemDetail("p9", "Two Sum", notes = "Use a hash map")
        val problems = FakeProblemRepository().apply {
            createHandler = { created }
            getHandler = { id -> problemDetail(id, "Two Sum") }
        }
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), FakeSolutionRepository(), backgroundScope)

        viewModel.newProblem()
        viewModel.editorTitleChanged("Two Sum")
        viewModel.saveProblem()
        advanceUntilIdle()

        assertEquals(ProblemsView.DETAIL, viewModel.state.value.view)
        assertEquals("p9", viewModel.state.value.detail!!.detail.summary.id)
        assertEquals("Two Sum", problems.created.single().title)
    }

    @Test
    fun openingAProblemLoadsItsDetailAndSolutions() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            getHandler = { id -> problemDetail(id, "Two Sum", notes = "Use a hash map") }
        }
        val solutions = FakeSolutionRepository().apply {
            listHandler = { problemId -> listOf(problemSolution("s1", problemId)) }
        }
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), solutions, backgroundScope)

        viewModel.openProblem("p1")
        advanceUntilIdle()

        assertEquals(ProblemsView.DETAIL, viewModel.state.value.view)
        assertEquals("Use a hash map", viewModel.state.value.detail!!.detail.notes)
        assertEquals(1, viewModel.state.value.detail!!.solutions.size)
    }

    @Test
    fun deletingAProblemRequiresConfirmation() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            getHandler = { id -> problemDetail(id, "Two Sum") }
        }
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), FakeSolutionRepository(), backgroundScope)
        viewModel.openProblem("p1")
        advanceUntilIdle()

        viewModel.confirmDeleteProblem()
        advanceUntilIdle()
        assertTrue(problems.deleted.isEmpty())

        viewModel.requestDeleteProblem()
        assertTrue(viewModel.state.value.detail!!.confirmingDelete)

        viewModel.confirmDeleteProblem()
        advanceUntilIdle()

        assertEquals(listOf("p1"), problems.deleted)
        assertEquals(ProblemsView.LIST, viewModel.state.value.view)
        assertEquals(ProblemsMessage.PROBLEM_DELETED, viewModel.state.value.message)
    }

    @Test
    fun solutionsAreSavedAndDeletedIndependently() = runTest(UnconfinedTestDispatcher()) {
        val problems = FakeProblemRepository().apply {
            getHandler = { id -> problemDetail(id, "Two Sum") }
        }
        val solutions = FakeSolutionRepository()
        val viewModel = ProblemsViewModel(problems, FakeTagRepository(), solutions, backgroundScope)
        viewModel.openProblem("p1")
        advanceUntilIdle()

        viewModel.solutionCodeChanged("return 1")
        viewModel.saveSolution()
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.detail!!.solutions.size)
        assertTrue(viewModel.state.value.detail!!.code.isEmpty())
        assertEquals("return 1", solutions.created.single().second.code)

        viewModel.deleteSolution("solution-new")
        advanceUntilIdle()
        assertTrue(viewModel.state.value.detail!!.solutions.isEmpty())
        assertEquals(listOf("solution-new"), solutions.deleted)
    }

    @Test
    fun creatingATagAppendsItAndClearsTheInput() = runTest(UnconfinedTestDispatcher()) {
        val tags = FakeTagRepository()
        val viewModel = ProblemsViewModel(FakeProblemRepository(), tags, FakeSolutionRepository(), backgroundScope)

        viewModel.newProblem()
        viewModel.editorTagNameChanged("array")
        viewModel.createTag()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.tags.any { it.name == "array" })
        assertEquals("", viewModel.state.value.editor!!.tagName)
        assertEquals(listOf("array"), tags.created)
    }
}
