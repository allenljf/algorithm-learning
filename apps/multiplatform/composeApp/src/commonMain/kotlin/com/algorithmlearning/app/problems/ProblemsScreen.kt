package com.algorithmlearning.app.problems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import com.algorithmlearning.shared.library.ProblemDifficulty
import com.algorithmlearning.shared.library.ProblemPlatform
import com.algorithmlearning.shared.library.ReviewStatus
import com.algorithmlearning.shared.library.SolutionLanguage

/** Every event the problem-management screen can emit. */
data class ProblemsActions(
    val refresh: () -> Unit = {},
    val searchChanged: (String) -> Unit = {},
    val search: () -> Unit = {},
    val difficultyFilterChanged: (ProblemDifficulty?) -> Unit = {},
    val platformFilterChanged: (ProblemPlatform?) -> Unit = {},
    val reviewStatusFilterChanged: (ReviewStatus?) -> Unit = {},
    val toggleTag: (String) -> Unit = {},
    val previousPage: () -> Unit = {},
    val nextPage: () -> Unit = {},
    val openProblem: (String) -> Unit = {},
    val newProblem: () -> Unit = {},
    val editProblem: () -> Unit = {},
    val cancelEditor: () -> Unit = {},
    val editorTitleChanged: (String) -> Unit = {},
    val editorPlatformChanged: (ProblemPlatform) -> Unit = {},
    val editorDifficultyChanged: (ProblemDifficulty) -> Unit = {},
    val editorNotesChanged: (String) -> Unit = {},
    val editorTagNameChanged: (String) -> Unit = {},
    val toggleEditorTag: (String) -> Unit = {},
    val saveProblem: () -> Unit = {},
    val createTag: () -> Unit = {},
    val requestDeleteProblem: () -> Unit = {},
    val cancelDeleteProblem: () -> Unit = {},
    val confirmDeleteProblem: () -> Unit = {},
    val backToList: () -> Unit = {},
    val solutionLanguageChanged: (SolutionLanguage) -> Unit = {},
    val solutionCodeChanged: (String) -> Unit = {},
    val solutionExplanationChanged: (String) -> Unit = {},
    val saveSolution: () -> Unit = {},
    val deleteSolution: (String) -> Unit = {},
)

@Composable
fun ProblemsScreen(
    state: ProblemsUiState,
    strings: AppStrings,
    actions: ProblemsActions = ProblemsActions(),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state.view) {
            ProblemsView.LIST -> ProblemLibrary(state, strings, actions)
            ProblemsView.EDITOR -> ProblemEditor(state, strings, actions)
            ProblemsView.DETAIL -> ProblemDetailPane(state, strings, actions)
        }
        if (state.view == ProblemsView.LIST) {
            FloatingActionButton(
                onClick = actions.newProblem,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("problem-add"),
            ) {
                Text(strings.problemAddAction)
            }
        }
    }
}

@Composable
private fun ProblemLibrary(
    state: ProblemsUiState,
    strings: AppStrings,
    actions: ProblemsActions,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        FilterBar(state, strings, actions)
        when (val list = state.list) {
            ProblemListState.Loading -> Box(
                modifier = Modifier.fillMaxSize().testTag("problems-loading"),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            is ProblemListState.Failed -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(strings.problemLoadFailureMessage)
                    Text(list.error.label(strings), style = MaterialTheme.typography.bodySmall)
                    Button(
                        onClick = actions.refresh,
                        modifier = Modifier.padding(top = 8.dp).testTag("problems-retry"),
                    ) {
                        Text(strings.problemRetryAction)
                    }
                }
            }
            is ProblemListState.Content -> if (list.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().testTag("problems-empty"),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(strings.problemEmptyMessage)
                        Button(
                            onClick = actions.newProblem,
                            modifier = Modifier.padding(top = 8.dp),
                        ) {
                            Text(strings.problemAddAction)
                        }
                    }
                }
            } else {
                ProblemList(list, strings, actions)
            }
        }
    }
}

@Composable
private fun FilterBar(
    state: ProblemsUiState,
    strings: AppStrings,
    actions: ProblemsActions,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.searchText,
                onValueChange = actions.searchChanged,
                label = { Text(strings.problemSearchLabel) },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("problem-search"),
            )
            TextButton(
                onClick = actions.search,
                modifier = Modifier.testTag("problem-search-submit"),
            ) {
                Text(strings.problemSearchAction)
            }
            TextButton(
                onClick = actions.refresh,
                modifier = Modifier.testTag("problems-refresh"),
            ) {
                Text(strings.problemRefreshAction)
            }
        }
        ChipGroup(
            label = strings.problemDifficultyLabel,
            options = ProblemDifficulty.entries.map { difficulty ->
                FilterOption(
                    text = difficulty.label(strings),
                    selected = state.query.difficulty == difficulty,
                    onClick = {
                        actions.difficultyFilterChanged(
                            if (state.query.difficulty == difficulty) null else difficulty,
                        )
                    },
                )
            },
        )
        ChipGroup(
            label = strings.problemPlatformLabel,
            options = ProblemPlatform.entries.map { platform ->
                FilterOption(
                    text = platform.label(strings),
                    selected = state.query.platform == platform,
                    onClick = {
                        actions.platformFilterChanged(
                            if (state.query.platform == platform) null else platform,
                        )
                    },
                )
            },
        )
        ChipGroup(
            label = strings.problemReviewStatusLabel,
            options = ReviewStatus.entries.map { status ->
                FilterOption(
                    text = status.label(strings),
                    selected = state.query.reviewStatus == status,
                    onClick = {
                        actions.reviewStatusFilterChanged(
                            if (state.query.reviewStatus == status) null else status,
                        )
                    },
                )
            },
        )
        if (state.tags.isNotEmpty()) {
            ChipGroup(
                label = strings.problemTagsLabel,
                options = state.tags.map { tag ->
                    FilterOption(
                        text = tag.name,
                        selected = state.query.tagIds.contains(tag.id),
                        onClick = { actions.toggleTag(tag.id) },
                    )
                },
            )
        }
    }
}

@Composable
private fun ProblemList(
    list: ProblemListState.Content,
    strings: AppStrings,
    actions: ProblemsActions,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        items(list.items, key = { it.id }) { problem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
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
                    Text(
                        text = problem.review.status.label(strings),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        item {
            Pagination(list, strings, actions)
        }
    }
}

@Composable
private fun Pagination(
    list: ProblemListState.Content,
    strings: AppStrings,
    actions: ProblemsActions,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = actions.previousPage,
            enabled = list.page > 1,
            modifier = Modifier.testTag("problems-previous"),
        ) {
            Text(strings.problemPreviousPageAction)
        }
        Text(
            text = "${strings.problemPageLabel} ${list.page} / ${list.totalPages}",
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        TextButton(
            onClick = actions.nextPage,
            enabled = list.page < list.totalPages,
            modifier = Modifier.testTag("problems-next"),
        ) {
            Text(strings.problemNextPageAction)
        }
    }
}

@Composable
private fun ProblemEditor(
    state: ProblemsUiState,
    strings: AppStrings,
    actions: ProblemsActions,
) {
    val editor = state.editor ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = if (editor.problemId == null) strings.problemNewTitle else strings.problemEditTitle,
            style = MaterialTheme.typography.headlineSmall,
        )
        val titleError = when {
            editor.titleMissing -> strings.problemTitleRequiredMessage
            editor.fieldErrors["title"] != null -> editor.fieldErrors.getValue("title").joinToString(", ")
            else -> null
        }
        OutlinedTextField(
            value = editor.title,
            onValueChange = actions.editorTitleChanged,
            label = { Text(strings.problemTitleLabel) },
            singleLine = true,
            isError = titleError != null,
            supportingText = if (titleError != null) {
                { Text(titleError) }
            } else {
                null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .testTag("problem-title"),
        )
        ChipGroup(
            label = strings.problemPlatformLabel,
            options = ProblemPlatform.entries.map { platform ->
                FilterOption(
                    text = platform.label(strings),
                    selected = editor.platform == platform,
                    onClick = { actions.editorPlatformChanged(platform) },
                )
            },
        )
        ChipGroup(
            label = strings.problemDifficultyLabel,
            options = ProblemDifficulty.entries.map { difficulty ->
                FilterOption(
                    text = difficulty.label(strings),
                    selected = editor.difficulty == difficulty,
                    onClick = { actions.editorDifficultyChanged(difficulty) },
                )
            },
        )
        OutlinedTextField(
            value = editor.notes,
            onValueChange = actions.editorNotesChanged,
            label = { Text(strings.problemNotesLabel) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )
        if (state.tags.isNotEmpty()) {
            ChipGroup(
                label = strings.problemTagsLabel,
                options = state.tags.map { tag ->
                    FilterOption(
                        text = tag.name,
                        selected = editor.tagIds.contains(tag.id),
                        onClick = { actions.toggleEditorTag(tag.id) },
                    )
                },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = editor.tagName,
                onValueChange = actions.editorTagNameChanged,
                label = { Text(strings.problemNewTagLabel) },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("problem-tag-name"),
            )
            TextButton(
                onClick = actions.createTag,
                modifier = Modifier.testTag("problem-tag-create"),
            ) {
                Text(strings.problemCreateTagAction)
            }
        }
        editor.formError?.let { failure ->
            Text(
                text = failure.label(strings),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(
                onClick = actions.saveProblem,
                enabled = !editor.saving,
                modifier = Modifier.testTag("problem-save"),
            ) {
                Text(strings.problemSaveAction)
            }
            TextButton(
                onClick = actions.cancelEditor,
                modifier = Modifier.padding(start = 8.dp).testTag("problem-cancel"),
            ) {
                Text(strings.commonCancelAction)
            }
        }
    }
}

@Composable
private fun ProblemDetailPane(
    state: ProblemsUiState,
    strings: AppStrings,
    actions: ProblemsActions,
) {
    val detail = state.detail ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        TextButton(onClick = actions.backToList) {
            Text(strings.problemDetailBackAction)
        }
        Text(detail.detail.summary.title, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = detail.detail.summary.difficulty.label(strings) + " · " +
                detail.detail.summary.platform.label(strings),
        )
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp)) {
            detail.detail.summary.tags.forEach { tag ->
                AssistChip(
                    onClick = {},
                    label = { Text(tag.name) },
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
        }
        Section(strings.problemDescriptionLabel, detail.detail.description)
        Section(strings.problemNotesLabel, detail.detail.notes)
        Section(strings.problemKeyInsightLabel, detail.detail.keyInsight)
        Section(strings.problemTimeComplexityLabel, detail.detail.timeComplexity)
        Section(strings.problemSpaceComplexityLabel, detail.detail.spaceComplexity)
        Section(strings.problemMistakesLabel, detail.detail.mistakes)
        Section(strings.problemInterviewNotesLabel, detail.detail.interviewNotes)
        detail.formError?.let { failure ->
            Text(
                text = failure.label(strings),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(strings.solutionsTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = actions.editProblem,
                modifier = Modifier.testTag("problem-edit"),
            ) {
                Text(strings.problemEditAction)
            }
            TextButton(
                onClick = actions.requestDeleteProblem,
                modifier = Modifier.testTag("problem-delete"),
            ) {
                Text(strings.problemDeleteAction)
            }
        }
        detail.solutions.forEach { solution ->
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(solution.language.name, style = MaterialTheme.typography.titleMedium)
                    Text(solution.code)
                    solution.explanation?.let { Text(it) }
                    TextButton(
                        onClick = { actions.deleteSolution(solution.id) },
                        modifier = Modifier.testTag("solution-delete-${solution.id}"),
                    ) {
                        Text(strings.solutionDeleteAction)
                    }
                }
            }
        }
        Text(
            text = strings.solutionLanguageLabel,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        ChipGroup(
            label = "",
            options = SolutionLanguage.entries.map { language ->
                FilterOption(
                    text = language.name,
                    selected = detail.language == language,
                    onClick = { actions.solutionLanguageChanged(language) },
                )
            },
        )
        OutlinedTextField(
            value = detail.code,
            onValueChange = actions.solutionCodeChanged,
            label = { Text(strings.solutionCodeLabel) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("solution-code"),
        )
        OutlinedTextField(
            value = detail.explanation,
            onValueChange = actions.solutionExplanationChanged,
            label = { Text(strings.solutionExplanationLabel) },
            minLines = 2,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
        Button(
            onClick = actions.saveSolution,
            enabled = !detail.savingSolution && detail.code.isNotBlank(),
            modifier = Modifier.padding(top = 12.dp).testTag("solution-save"),
        ) {
            Text(strings.solutionSaveAction)
        }
    }

    if (detail.confirmingDelete) {
        AlertDialog(
            onDismissRequest = actions.cancelDeleteProblem,
            title = { Text(strings.problemDeleteConfirmTitle) },
            text = { Text(strings.problemDeleteConfirmMessage) },
            confirmButton = {
                TextButton(onClick = actions.confirmDeleteProblem) {
                    Text(strings.commonDeleteAction)
                }
            },
            dismissButton = {
                TextButton(onClick = actions.cancelDeleteProblem) {
                    Text(strings.commonCancelAction)
                }
            },
        )
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

private data class FilterOption(
    val text: String,
    val selected: Boolean,
    val onClick: () -> Unit,
)

@Composable
private fun ChipGroup(label: String, options: List<FilterOption>) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()).padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label.isNotEmpty()) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.width(8.dp))
        }
        options.forEach { option ->
            FilterChip(
                selected = option.selected,
                onClick = option.onClick,
                label = { Text(option.text) },
                modifier = Modifier.padding(end = 8.dp),
            )
        }
    }
}
