package com.algorithmlearning.app.review

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.algorithmlearning.app.label
import com.algorithmlearning.shared.AppStrings
import com.algorithmlearning.shared.library.ProblemDetail

/** Every event the review experience can emit. */
data class ReviewActions(
    val refreshDue: () -> Unit = {},
    val openProblem: (String) -> Unit = {},
    val exitReview: () -> Unit = {},
    val startThinking: () -> Unit = {},
    val revealHint: () -> Unit = {},
    val revealApproach: () -> Unit = {},
    val revealSolution: () -> Unit = {},
    val rateConfidence: () -> Unit = {},
    val selectSolution: (String) -> Unit = {},
    val confidenceSelected: (Int) -> Unit = {},
    val notesChanged: (String) -> Unit = {},
    val submitReview: () -> Unit = {},
)

/**
 * The review experience. It adapts between a single pane (mobile) and a
 * browse/session two-pane layout (wide) while rendering the same state.
 */
@Composable
fun ReviewScreen(
    state: ReviewUiState,
    strings: AppStrings,
    actions: ReviewActions = ReviewActions(),
    twoPane: Boolean = false,
) {
    if (twoPane) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                ReviewBrowse(state, strings, actions)
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Box(modifier = Modifier.weight(1.4f).fillMaxHeight()) {
                ReviewPane(state, strings, actions, promptWhenEmpty = true)
            }
        }
    } else {
        ReviewPane(state, strings, actions, promptWhenEmpty = false)
    }
}

@Composable
private fun ReviewPane(
    state: ReviewUiState,
    strings: AppStrings,
    actions: ReviewActions,
    promptWhenEmpty: Boolean,
) {
    when {
        state.sessionLoading -> Box(
            modifier = Modifier.fillMaxSize().testTag("review-session-loading"),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        state.session != null -> ReviewSession(state.session, strings, actions)
        state.sessionError != null -> Box(
            modifier = Modifier.fillMaxSize().testTag("review-session-error"),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(strings.reviewLoadFailureMessage)
                Text(state.sessionError.label(strings), style = MaterialTheme.typography.bodySmall)
                Button(
                    onClick = actions.exitReview,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text(strings.reviewExitAction)
                }
            }
        }
        promptWhenEmpty -> Box(
            modifier = Modifier.fillMaxSize().testTag("review-select-prompt"),
            contentAlignment = Alignment.Center,
        ) {
            Text(strings.reviewSelectPrompt)
        }
        else -> ReviewBrowse(state, strings, actions)
    }
}

@Composable
private fun ReviewBrowse(
    state: ReviewUiState,
    strings: AppStrings,
    actions: ReviewActions,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(strings.reviewTitle, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = actions.refreshDue,
                modifier = Modifier.testTag("review-refresh"),
            ) {
                Text(strings.problemRefreshAction)
            }
        }
        when (val due = state.due) {
            DueReviewState.Loading -> Box(
                modifier = Modifier.fillMaxSize().testTag("review-loading"),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            is DueReviewState.Failed -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(strings.reviewLoadFailureMessage)
                    Text(due.error.label(strings), style = MaterialTheme.typography.bodySmall)
                    Button(
                        onClick = actions.refreshDue,
                        modifier = Modifier.padding(top = 8.dp).testTag("review-retry"),
                    ) {
                        Text(strings.problemRetryAction)
                    }
                }
            }
            is DueReviewState.Content -> if (due.problems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().testTag("review-empty"),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(strings.reviewDueEmptyMessage)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(due.problems, key = { it.id }) { problem ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { actions.openProblem(problem.id) }
                                    .padding(12.dp),
                            ) {
                                Text(problem.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = problem.difficulty.label(strings) + " · " +
                                        problem.tags.joinToString(", ") { it.name },
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewSession(
    session: ReviewSessionState,
    strings: AppStrings,
    actions: ReviewActions,
) {
    val problem = session.problem
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        TextButton(onClick = actions.exitReview) {
            Text(strings.reviewExitAction)
        }
        Text(problem.summary.title, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = problem.summary.difficulty.label(strings) + " · " +
                problem.summary.platform.label(strings),
        )
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp)) {
            problem.summary.tags.forEach { tag ->
                Text(
                    text = tag.name,
                    modifier = Modifier.padding(end = 12.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
        Section(strings.problemDescriptionLabel, problem.description)

        if (session.stage.revealedAtLeast(ReviewStage.THINK)) {
            Text(
                text = strings.reviewThinkInstruction,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
        if (session.stage.revealedAtLeast(ReviewStage.HINT)) {
            Section(strings.problemKeyInsightLabel, problem.keyInsight ?: strings.reviewNoHintMessage)
        }
        if (session.stage.revealedAtLeast(ReviewStage.APPROACH)) {
            Section(strings.problemNotesLabel, problem.notes ?: strings.reviewNoApproachMessage)
            Section(strings.problemMistakesLabel, problem.mistakes)
            Section(strings.problemTimeComplexityLabel, problem.timeComplexity)
            Section(strings.problemSpaceComplexityLabel, problem.spaceComplexity)
        }
        if (session.stage.revealedAtLeast(ReviewStage.SOLUTION)) {
            SolutionStage(session, strings, actions)
        }

        if (session.submitted) {
            Text(
                text = strings.reviewSubmittedMessage,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp).testTag("review-submitted"),
            )
            Button(
                onClick = actions.exitReview,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(strings.reviewExitAction)
            }
        } else {
            StageAction(session, strings, actions)
        }
    }
}

@Composable
private fun SolutionStage(
    session: ReviewSessionState,
    strings: AppStrings,
    actions: ReviewActions,
) {
    val solutions = session.problem.solutions
    Text(
        text = strings.reviewSolutionLabel,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 12.dp),
    )
    if (solutions.isEmpty()) {
        Text(strings.reviewNoApproachMessage)
        return
    }
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
    ) {
        solutions.forEach { solution ->
            FilterChip(
                selected = session.selectedSolutionId == solution.id,
                onClick = { actions.selectSolution(solution.id) },
                label = { Text(solution.language.name) },
                modifier = Modifier.padding(end = 8.dp),
            )
        }
    }
    val selected = solutions.firstOrNull { it.id == session.selectedSolutionId } ?: solutions.first()
    selected.explanation?.let { Text(it) }
    Text(selected.code, modifier = Modifier.testTag("review-solution-code"))
}

@Composable
private fun StageAction(
    session: ReviewSessionState,
    strings: AppStrings,
    actions: ReviewActions,
) {
    val (label, action) = when (session.stage) {
        ReviewStage.PROBLEM -> strings.reviewStartThinkingAction to actions.startThinking
        ReviewStage.THINK -> strings.reviewRevealHintAction to actions.revealHint
        ReviewStage.HINT -> strings.reviewRevealApproachAction to actions.revealApproach
        ReviewStage.APPROACH -> strings.reviewRevealSolutionAction to actions.revealSolution
        ReviewStage.SOLUTION -> strings.reviewRateConfidenceAction to actions.rateConfidence
        ReviewStage.CONFIDENCE -> "" to {}
    }
    if (session.stage == ReviewStage.CONFIDENCE) {
        ConfidenceControls(session, strings, actions)
        return
    }
    Button(
        onClick = action,
        modifier = Modifier.padding(top = 16.dp).testTag("review-stage-action"),
    ) {
        Text(label)
    }
}

@Composable
private fun ConfidenceControls(
    session: ReviewSessionState,
    strings: AppStrings,
    actions: ReviewActions,
) {
    Text(
        text = strings.reviewConfidenceLabel,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp),
    )
    Text(strings.reviewConfidenceHintMessage, style = MaterialTheme.typography.bodySmall)
    Row {
        (0..4).forEach { value ->
            FilterChip(
                selected = session.confidence == value,
                onClick = { actions.confidenceSelected(value) },
                label = { Text(value.toString()) },
                modifier = Modifier.padding(end = 8.dp).testTag("review-confidence-$value"),
            )
        }
    }
    OutlinedTextField(
        value = session.notes,
        onValueChange = actions.notesChanged,
        label = { Text(strings.reviewNoteLabel) },
        minLines = 2,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("review-note"),
    )
    session.error?.let { failure ->
        Text(
            text = failure.label(strings),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
    Button(
        onClick = actions.submitReview,
        enabled = session.confidence != null && !session.submitting,
        modifier = Modifier.padding(top = 12.dp).testTag("review-submit"),
    ) {
        Text(strings.reviewSubmitAction)
    }
}

@Composable
private fun Section(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Text(value)
    }
}

private fun ReviewStage.revealedAtLeast(other: ReviewStage): Boolean = ordinal >= other.ordinal
