package com.algorithmlearning.app.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.algorithmlearning.app.label
import com.algorithmlearning.shared.AppStrings

/** Events from the scheduled-review list. Opening an item always enters Detail. */
data class ReviewActions(
    val refreshDue: () -> Unit = {},
    val openProblem: (String) -> Unit = {},
)

/**
 * Scheduled review remains a due-problem index. Learning itself happens in the
 * complete Problem Detail screen, so there is no staged disclosure or rating UI.
 */
@Composable
fun ReviewScreen(
    state: ReviewUiState,
    strings: AppStrings,
    actions: ReviewActions = ReviewActions(),
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(strings.reviewTitle, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = actions.refreshDue, modifier = Modifier.testTag("review-refresh")) {
                Text(strings.problemRefreshAction)
            }
        }
        when (val due = state.due) {
            DueReviewState.Loading -> Box(
                modifier = Modifier.fillMaxSize().testTag("review-loading"),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            is DueReviewState.Failed -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(strings.reviewLoadFailureMessage)
                    Text(due.error.label(strings), style = MaterialTheme.typography.bodySmall)
                    Button(onClick = actions.refreshDue, modifier = Modifier.padding(top = 8.dp).testTag("review-retry")) {
                        Text(strings.problemRetryAction)
                    }
                }
            }
            is DueReviewState.Content -> if (due.problems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().testTag("review-empty"),
                    contentAlignment = Alignment.Center,
                ) { Text(strings.reviewDueEmptyMessage) }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(due.problems, key = { it.id }) { problem ->
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { actions.openProblem(problem.id) }
                                    .padding(12.dp)
                                    .testTag("review-open-${problem.id}"),
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
