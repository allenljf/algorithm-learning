package com.algorithmlearning.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.algorithmlearning.app.dashboard.DashboardActions
import com.algorithmlearning.app.dashboard.DashboardScreen
import com.algorithmlearning.app.dashboard.DashboardState
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.StringCatalog
import com.algorithmlearning.shared.library.ApiFailureKind
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class DashboardScreenUiTest {

    private val strings = StringCatalog.of(AppLanguage.ENGLISH)

    @Test
    fun loadingStateRendersProgress() = runComposeUiTest {
        setContent { DashboardScreen(state = DashboardState.Loading, strings = strings) }
        onNodeWithTag("dashboard-loading").assertIsDisplayed()
    }

    @Test
    fun failureStateRetries() = runComposeUiTest {
        var refreshes = 0
        setContent {
            DashboardScreen(
                state = DashboardState.Failed(ApiFailureKind.NETWORK),
                strings = strings,
                actions = DashboardActions(refresh = { refreshes++ }),
            )
        }
        onNodeWithText(strings.dashboardLoadFailureMessage).assertIsDisplayed()
        onNodeWithTag("dashboard-retry").performClick()
        assertEquals(1, refreshes)
    }

    @Test
    fun emptyStateInvitesAddingAProblem() = runComposeUiTest {
        var addClicks = 0
        setContent {
            DashboardScreen(
                state = DashboardState.Content(dashboard(totalProblems = 0)),
                strings = strings,
                actions = DashboardActions(addProblem = { addClicks++ }),
            )
        }
        onNodeWithText(strings.dashboardEmptyMessage).assertIsDisplayed()
        onNodeWithTag("dashboard-add").performClick()
        assertEquals(1, addClicks)
    }

    @Test
    fun populatedStateShowsTotalsDistributionAndDueCount() = runComposeUiTest {
        val state = DashboardState.Content(
            dashboard(totalProblems = 5, easy = 2, medium = 2, hard = 1, dueReviewCount = 3),
        )
        setContent { DashboardScreen(state = state, strings = strings) }
        onNodeWithTag("dashboard-total").assertTextEquals("5")
        onNodeWithTag("dashboard-easy").assertTextEquals("2")
        onNodeWithTag("dashboard-medium").assertTextEquals("2")
        onNodeWithTag("dashboard-hard").assertTextEquals("1")
        onNodeWithTag("dashboard-due").assertTextEquals("3")
    }
}
