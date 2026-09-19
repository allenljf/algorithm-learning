package com.algorithmlearning.app.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import com.algorithmlearning.shared.library.Dashboard

/** Every event the dashboard/home screen can emit. */
data class DashboardActions(
    val refresh: () -> Unit = {},
    val addProblem: () -> Unit = {},
)

/**
 * Dashboard/home aggregate states: loading, retry, no-data guidance, and the
 * populated totals, difficulty distribution, and due-review count.
 */
@Composable
fun DashboardScreen(
    state: DashboardState,
    strings: AppStrings,
    actions: DashboardActions = DashboardActions(),
) {
    when (state) {
        DashboardState.Loading -> Box(
            modifier = Modifier.fillMaxSize().testTag("dashboard-loading"),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        is DashboardState.Failed -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(strings.dashboardLoadFailureMessage)
                Text(state.error.label(strings), style = MaterialTheme.typography.bodySmall)
                Button(
                    onClick = actions.refresh,
                    modifier = Modifier.padding(top = 8.dp).testTag("dashboard-retry"),
                ) {
                    Text(strings.problemRetryAction)
                }
            }
        }
        is DashboardState.Content -> if (state.dashboard.totalProblems == 0L) {
            Box(
                modifier = Modifier.fillMaxSize().testTag("dashboard-empty"),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(strings.dashboardEmptyMessage)
                    Button(
                        onClick = actions.addProblem,
                        modifier = Modifier.padding(top = 8.dp).testTag("dashboard-add"),
                    ) {
                        Text(strings.problemAddAction)
                    }
                }
            }
        } else {
            DashboardContent(state.dashboard, strings, actions)
        }
    }
}

@Composable
private fun DashboardContent(
    dashboard: Dashboard,
    strings: AppStrings,
    actions: DashboardActions,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(strings.dashboardTitle, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = actions.refresh,
                modifier = Modifier.testTag("dashboard-refresh"),
            ) {
                Text(strings.problemRefreshAction)
            }
        }
        TotalRow(strings.dashboardTotalLabel, dashboard.totalProblems, "dashboard-total")
        TotalRow(strings.dashboardDueReviewsLabel, dashboard.dueReviewCount, "dashboard-due")
        Text(
            text = strings.dashboardDifficultyLabel,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        DistributionRow(strings.difficultyEasy, dashboard.easy, dashboard.totalProblems, "dashboard-easy")
        DistributionRow(strings.difficultyMedium, dashboard.medium, dashboard.totalProblems, "dashboard-medium")
        DistributionRow(strings.difficultyHard, dashboard.hard, dashboard.totalProblems, "dashboard-hard")
    }
}

@Composable
private fun TotalRow(label: String, value: Long, tag: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.testTag(tag),
        )
    }
}

@Composable
private fun DistributionRow(label: String, value: Long, total: Long, tag: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label)
            Spacer(modifier = Modifier.weight(1f))
            Text(value.toString(), modifier = Modifier.testTag(tag))
        }
        LinearProgressIndicator(
            progress = { if (total > 0L) value.toFloat() / total.toFloat() else 0f },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        )
    }
}
