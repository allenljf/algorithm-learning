package com.algorithmlearning.app.review

import com.algorithmlearning.shared.library.ApiFailure
import com.algorithmlearning.shared.library.ApiFailureKind
import com.algorithmlearning.shared.library.ProblemDetail
import com.algorithmlearning.shared.library.ProblemRepository
import com.algorithmlearning.shared.library.ProblemSummary
import com.algorithmlearning.shared.library.ReviewRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The ordered disclosure stages of Review Mode. It is monotonic: a stage can be
 * advanced to only from its predecessor, so confidence can never be reached
 * before the solution is revealed.
 */
enum class ReviewStage { PROBLEM, THINK, HINT, APPROACH, SOLUTION, CONFIDENCE }

/** The Today's Review browse list states. */
sealed interface DueReviewState {
    data object Loading : DueReviewState

    data class Content(val problems: List<ProblemSummary>) : DueReviewState

    data class Failed(val error: ApiFailureKind) : DueReviewState
}

/** One problem's in-progress review session. */
data class ReviewSessionState(
    val problem: ProblemDetail,
    val stage: ReviewStage = ReviewStage.PROBLEM,
    val selectedSolutionId: String? = null,
    val confidence: Int? = null,
    val notes: String = "",
    val submitting: Boolean = false,
    val submitted: Boolean = false,
    val error: ApiFailureKind? = null,
)

/** One immutable snapshot of the whole review experience. */
data class ReviewUiState(
    val due: DueReviewState = DueReviewState.Loading,
    val session: ReviewSessionState? = null,
    val sessionLoading: Boolean = false,
    val sessionError: ApiFailureKind? = null,
)

/**
 * Presentation state holder for Today's Review and the staged Review Mode. It
 * depends only on the shared repository contracts; it never touches HTTP types
 * and never renders a string.
 */
class ReviewViewModel(
    private val reviews: ReviewRepository,
    private val problems: ProblemRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(ReviewUiState())
    val state: StateFlow<ReviewUiState> = _state.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadDue()
    }

    fun refreshDue() = loadDue()

    fun openProblem(id: String) {
        _state.update { it.copy(session = null, sessionLoading = true, sessionError = null) }
        scope.launch {
            try {
                val detail = problems.get(id)
                _state.update {
                    it.copy(
                        sessionLoading = false,
                        session = ReviewSessionState(
                            problem = detail,
                            selectedSolutionId = detail.solutions.firstOrNull()?.id,
                        ),
                    )
                }
            } catch (failure: ApiFailure) {
                _state.update { it.copy(sessionLoading = false, sessionError = failure.kind) }
            }
        }
    }

    fun exitReview() = _state.update {
        it.copy(session = null, sessionLoading = false, sessionError = null)
    }

    fun startThinking() = advance(ReviewStage.PROBLEM, ReviewStage.THINK)

    fun revealHint() = advance(ReviewStage.THINK, ReviewStage.HINT)

    fun revealApproach() = advance(ReviewStage.HINT, ReviewStage.APPROACH)

    fun revealSolution() = advance(ReviewStage.APPROACH, ReviewStage.SOLUTION)

    fun rateConfidence() = advance(ReviewStage.SOLUTION, ReviewStage.CONFIDENCE)

    fun selectSolution(id: String) = updateSession { it.copy(selectedSolutionId = id) }

    fun confidenceSelected(value: Int) {
        if (value !in 0..4) return
        updateSession { it.copy(confidence = value) }
    }

    fun notesChanged(value: String) = updateSession { it.copy(notes = value) }

    fun submitReview() {
        val session = _state.value.session ?: return
        if (session.submitted || session.submitting) return
        if (session.stage != ReviewStage.CONFIDENCE) return
        val confidence = session.confidence ?: return
        updateSession { it.copy(submitting = true, error = null) }
        scope.launch {
            try {
                reviews.submit(
                    problemId = session.problem.summary.id,
                    confidence = confidence,
                    notes = session.notes.trim().ifBlank { null },
                )
                updateSession { it.copy(submitting = false, submitted = true) }
                loadDue()
            } catch (failure: ApiFailure) {
                updateSession { it.copy(submitting = false, error = failure.kind) }
            }
        }
    }

    private fun advance(from: ReviewStage, to: ReviewStage) = updateSession { session ->
        if (session.stage == from && !session.submitted) session.copy(stage = to) else session
    }

    private fun loadDue() {
        _state.update { it.copy(due = DueReviewState.Loading) }
        scope.launch {
            try {
                val due = reviews.due()
                _state.update { it.copy(due = DueReviewState.Content(due)) }
            } catch (failure: ApiFailure) {
                _state.update { it.copy(due = DueReviewState.Failed(failure.kind)) }
            }
        }
    }

    private fun updateSession(transform: (ReviewSessionState) -> ReviewSessionState) =
        _state.update { current -> current.copy(session = current.session?.let(transform)) }
}
