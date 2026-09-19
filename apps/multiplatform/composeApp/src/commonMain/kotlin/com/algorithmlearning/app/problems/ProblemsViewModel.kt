package com.algorithmlearning.app.problems

import com.algorithmlearning.shared.library.ApiFailure
import com.algorithmlearning.shared.library.ApiFailureKind
import com.algorithmlearning.shared.library.Page
import com.algorithmlearning.shared.library.ProblemDetail
import com.algorithmlearning.shared.library.ProblemDifficulty
import com.algorithmlearning.shared.library.ProblemPlatform
import com.algorithmlearning.shared.library.ProblemQuery
import com.algorithmlearning.shared.library.ProblemRepository
import com.algorithmlearning.shared.library.ProblemSort
import com.algorithmlearning.shared.library.ProblemSummary
import com.algorithmlearning.shared.library.ProblemWrite
import com.algorithmlearning.shared.library.ReviewStatus
import com.algorithmlearning.shared.library.Solution
import com.algorithmlearning.shared.library.SolutionLanguage
import com.algorithmlearning.shared.library.SolutionRepository
import com.algorithmlearning.shared.library.SolutionWrite
import com.algorithmlearning.shared.library.Tag
import com.algorithmlearning.shared.library.TagRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Which pane of the problem-management experience is visible. */
enum class ProblemsView { LIST, EDITOR, DETAIL }

/** A transient, non-blocking notice the screen renders once and consumes. */
enum class ProblemsMessage { PROBLEM_DELETED, OPERATION_FAILED }

/** The mutually exclusive states of the problem list. */
sealed interface ProblemListState {
    data object Loading : ProblemListState

    data class Content(
        val items: List<ProblemSummary>,
        val page: Int,
        val totalPages: Int,
        val totalItems: Int,
    ) : ProblemListState

    data class Failed(val error: ApiFailureKind) : ProblemListState
}

/** The create/edit draft. Field errors reported by the server are kept separate. */
data class ProblemEditorState(
    val problemId: String? = null,
    val title: String = "",
    val platform: ProblemPlatform = ProblemPlatform.LEETCODE,
    val difficulty: ProblemDifficulty = ProblemDifficulty.EASY,
    val notes: String = "",
    val tagIds: Set<String> = emptySet(),
    val tagName: String = "",
    val saving: Boolean = false,
    val titleMissing: Boolean = false,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
    val formError: ApiFailureKind? = null,
)

/** The detail pane plus its independent solution editor. */
data class ProblemDetailState(
    val detail: ProblemDetail,
    val solutions: List<Solution> = emptyList(),
    val language: SolutionLanguage = SolutionLanguage.KOTLIN,
    val code: String = "",
    val explanation: String = "",
    val savingSolution: Boolean = false,
    val confirmingDelete: Boolean = false,
    val formError: ApiFailureKind? = null,
)

/** One immutable snapshot of the whole experience; rendered top-down. */
data class ProblemsUiState(
    val list: ProblemListState = ProblemListState.Loading,
    val query: ProblemQuery = ProblemQuery(),
    val searchText: String = "",
    val tags: List<Tag> = emptyList(),
    val view: ProblemsView = ProblemsView.LIST,
    val editor: ProblemEditorState? = null,
    val detail: ProblemDetailState? = null,
    val message: ProblemsMessage? = null,
)

/**
 * Presentation state holder for problem management. It receives repositories
 * through its constructor (never a service locator), exposes immutable state,
 * and maps [ApiFailure] to presentation-level kinds. Composables read state and
 * emit events; they never fetch data.
 */
class ProblemsViewModel(
    private val problems: ProblemRepository,
    private val tags: TagRepository,
    private val solutions: SolutionRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(ProblemsUiState())
    val state: StateFlow<ProblemsUiState> = _state.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadTags()
        loadList()
    }

    fun refresh() = loadList()

    fun searchChanged(value: String) = _state.update { it.copy(searchText = value) }

    fun search() = applyQuery(_state.value.query.copy(query = _state.value.searchText.trim().ifBlank { null }, page = 1))

    fun difficultyFilterChanged(value: ProblemDifficulty?) =
        applyQuery(_state.value.query.copy(difficulty = value, page = 1))

    fun platformFilterChanged(value: ProblemPlatform?) =
        applyQuery(_state.value.query.copy(platform = value, page = 1))

    fun reviewStatusFilterChanged(value: ReviewStatus?) =
        applyQuery(_state.value.query.copy(reviewStatus = value, page = 1))

    fun sortChanged(value: ProblemSort) = applyQuery(_state.value.query.copy(sort = value, page = 1))

    fun toggleTagFilter(tagId: String) {
        val selected = _state.value.query.tagIds
        val next = if (selected.contains(tagId)) selected - tagId else selected + tagId
        applyQuery(_state.value.query.copy(tagIds = next, page = 1))
    }

    fun nextPage() {
        val content = _state.value.list as? ProblemListState.Content ?: return
        if (_state.value.query.page >= content.totalPages) return
        applyQuery(_state.value.query.copy(page = _state.value.query.page + 1))
    }

    fun previousPage() {
        if (_state.value.query.page <= 1) return
        applyQuery(_state.value.query.copy(page = _state.value.query.page - 1))
    }

    fun openProblem(id: String) = openProblemInternal(id)

    fun backToList() = _state.update { it.copy(view = ProblemsView.LIST, detail = null, editor = null) }

    fun newProblem() = _state.update {
        it.copy(view = ProblemsView.EDITOR, editor = ProblemEditorState(), detail = null)
    }

    fun editProblem() {
        val detail = _state.value.detail?.detail ?: return
        _state.update {
            it.copy(
                view = ProblemsView.EDITOR,
                editor = ProblemEditorState(
                    problemId = detail.summary.id,
                    title = detail.summary.title,
                    platform = detail.summary.platform,
                    difficulty = detail.summary.difficulty,
                    notes = detail.notes.orEmpty(),
                    tagIds = detail.summary.tags.map { tag -> tag.id }.toSet(),
                ),
            )
        }
    }

    fun cancelEditor() {
        val editor = _state.value.editor
        val returnToDetail = editor?.problemId != null && _state.value.detail != null
        _state.update {
            it.copy(
                view = if (returnToDetail) ProblemsView.DETAIL else ProblemsView.LIST,
                editor = null,
            )
        }
    }

    fun editorTitleChanged(value: String) = updateEditor { it.copy(title = value, titleMissing = false) }

    fun editorPlatformChanged(value: ProblemPlatform) = updateEditor { it.copy(platform = value) }

    fun editorDifficultyChanged(value: ProblemDifficulty) = updateEditor { it.copy(difficulty = value) }

    fun editorNotesChanged(value: String) = updateEditor { it.copy(notes = value) }

    fun editorTagNameChanged(value: String) = updateEditor { it.copy(tagName = value) }

    fun toggleEditorTag(tagId: String) = updateEditor {
        val next = if (it.tagIds.contains(tagId)) it.tagIds - tagId else it.tagIds + tagId
        it.copy(tagIds = next)
    }

    fun saveProblem() {
        val editor = _state.value.editor ?: return
        if (editor.saving) return
        if (editor.title.isBlank()) {
            updateEditor { it.copy(titleMissing = true) }
            return
        }
        updateEditor { it.copy(saving = true, titleMissing = false, fieldErrors = emptyMap(), formError = null) }
        scope.launch {
            try {
                val write = editor.toWrite()
                val saved = if (editor.problemId == null) {
                    problems.create(write)
                } else {
                    problems.replace(editor.problemId, write)
                }
                _state.update { it.copy(editor = null) }
                openProblemInternal(saved.summary.id)
                loadList()
            } catch (failure: ApiFailure) {
                updateEditor {
                    it.copy(
                        saving = false,
                        fieldErrors = failure.fieldErrors,
                        formError = if (failure.fieldErrors.isEmpty()) failure.kind else null,
                    )
                }
            }
        }
    }

    fun createTag() {
        val editor = _state.value.editor ?: return
        val name = editor.tagName.trim()
        if (name.isEmpty()) return
        scope.launch {
            try {
                val created = tags.create(name)
                _state.update { current ->
                    val existing = current.tags.any { it.id == created.id }
                    current.copy(
                        tags = if (existing) current.tags else current.tags + created,
                        editor = current.editor?.copy(tagName = ""),
                    )
                }
            } catch (failure: ApiFailure) {
                _state.update { it.copy(message = ProblemsMessage.OPERATION_FAILED) }
            }
        }
    }

    fun requestDeleteProblem() = updateDetail { it.copy(confirmingDelete = true) }

    fun cancelDeleteProblem() = updateDetail { it.copy(confirmingDelete = false) }

    fun confirmDeleteProblem() {
        val detail = _state.value.detail ?: return
        if (!detail.confirmingDelete) return
        scope.launch {
            try {
                problems.delete(detail.detail.summary.id)
                _state.update {
                    it.copy(view = ProblemsView.LIST, detail = null, message = ProblemsMessage.PROBLEM_DELETED)
                }
                loadList()
            } catch (failure: ApiFailure) {
                updateDetail { it.copy(confirmingDelete = false, formError = failure.kind) }
            }
        }
    }

    fun solutionLanguageChanged(value: SolutionLanguage) = updateDetail { it.copy(language = value) }

    fun solutionCodeChanged(value: String) = updateDetail { it.copy(code = value) }

    fun solutionExplanationChanged(value: String) = updateDetail { it.copy(explanation = value) }

    fun saveSolution() {
        val detail = _state.value.detail ?: return
        if (detail.savingSolution || detail.code.isBlank()) return
        updateDetail { it.copy(savingSolution = true, formError = null) }
        scope.launch {
            try {
                val saved = solutions.create(
                    detail.detail.summary.id,
                    SolutionWrite(
                        language = detail.language,
                        code = detail.code,
                        explanation = detail.explanation.trim().ifBlank { null },
                    ),
                )
                updateDetail {
                    it.copy(
                        solutions = it.solutions + saved,
                        code = "",
                        explanation = "",
                        savingSolution = false,
                    )
                }
            } catch (failure: ApiFailure) {
                updateDetail { it.copy(savingSolution = false, formError = failure.kind) }
            }
        }
    }

    fun deleteSolution(id: String) {
        scope.launch {
            try {
                solutions.delete(id)
                updateDetail { it.copy(solutions = it.solutions.filterNot { solution -> solution.id == id }) }
            } catch (failure: ApiFailure) {
                _state.update { it.copy(message = ProblemsMessage.OPERATION_FAILED) }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }

    private fun applyQuery(query: ProblemQuery) {
        _state.update { it.copy(query = query) }
        loadList()
    }

    private fun loadList() {
        _state.update { it.copy(list = ProblemListState.Loading) }
        scope.launch {
            try {
                val page: Page<ProblemSummary> = problems.list(_state.value.query)
                _state.update {
                    it.copy(
                        list = ProblemListState.Content(
                            items = page.items,
                            page = page.page,
                            totalPages = page.totalPages,
                            totalItems = page.totalItems,
                        ),
                    )
                }
            } catch (failure: ApiFailure) {
                _state.update { it.copy(list = ProblemListState.Failed(failure.kind)) }
            }
        }
    }

    private fun loadTags() {
        scope.launch {
            try {
                val loaded = tags.list()
                _state.update { it.copy(tags = loaded) }
            } catch (failure: ApiFailure) {
                _state.update { it.copy(message = ProblemsMessage.OPERATION_FAILED) }
            }
        }
    }

    private fun openProblemInternal(id: String) {
        scope.launch {
            try {
                val detail = problems.get(id)
                val loaded = solutions.list(id)
                _state.update {
                    it.copy(
                        view = ProblemsView.DETAIL,
                        detail = ProblemDetailState(detail = detail, solutions = loaded),
                    )
                }
            } catch (failure: ApiFailure) {
                _state.update { it.copy(message = ProblemsMessage.OPERATION_FAILED) }
            }
        }
    }

    private fun updateEditor(transform: (ProblemEditorState) -> ProblemEditorState) =
        _state.update { current -> current.copy(editor = current.editor?.let(transform)) }

    private fun updateDetail(transform: (ProblemDetailState) -> ProblemDetailState) =
        _state.update { current -> current.copy(detail = current.detail?.let(transform)) }
}

private fun ProblemEditorState.toWrite(): ProblemWrite = ProblemWrite(
    title = title.trim(),
    platform = platform,
    difficulty = difficulty,
    notes = notes.trim().ifBlank { null },
    tagIds = tagIds.toList(),
)
