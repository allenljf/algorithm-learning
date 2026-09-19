@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.algorithmlearning.app.dashboard

import com.algorithmlearning.app.FakeDashboardRepository
import com.algorithmlearning.app.dashboard
import com.algorithmlearning.shared.library.ApiFailure
import com.algorithmlearning.shared.library.ApiFailureKind
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DashboardViewModelTest {

    @Test
    fun loadExposesTotalsDistributionAndDueCount() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeDashboardRepository().apply {
            handler = { dashboard(totalProblems = 5, easy = 2, medium = 2, hard = 1, dueReviewCount = 3) }
        }
        val viewModel = DashboardViewModel(repository, backgroundScope)

        viewModel.load()
        advanceUntilIdle()

        val content = viewModel.state.value as DashboardState.Content
        assertEquals(5, content.dashboard.totalProblems)
        assertEquals(2, content.dashboard.easy)
        assertEquals(2, content.dashboard.medium)
        assertEquals(1, content.dashboard.hard)
        assertEquals(3, content.dashboard.dueReviewCount)
    }

    @Test
    fun failureIsDistinctAndRetryRecovers() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeDashboardRepository().apply {
            handler = { throw ApiFailure(ApiFailureKind.NETWORK) }
        }
        val viewModel = DashboardViewModel(repository, backgroundScope)

        viewModel.load()
        advanceUntilIdle()
        assertEquals(DashboardState.Failed(ApiFailureKind.NETWORK), viewModel.state.value)

        repository.handler = { dashboard(totalProblems = 1) }
        viewModel.refresh()
        advanceUntilIdle()

        assertTrue(viewModel.state.value is DashboardState.Content)
    }

    @Test
    fun reloadingFetchesFreshCounts() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeDashboardRepository()
        val viewModel = DashboardViewModel(repository, backgroundScope)

        viewModel.load()
        advanceUntilIdle()
        viewModel.load()
        advanceUntilIdle()

        assertEquals(2, repository.calls)
    }
}
