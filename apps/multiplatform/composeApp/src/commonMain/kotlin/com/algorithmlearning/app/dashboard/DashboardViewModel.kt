package com.algorithmlearning.app.dashboard

import com.algorithmlearning.shared.library.ApiFailure
import com.algorithmlearning.shared.library.ApiFailureKind
import com.algorithmlearning.shared.library.Dashboard
import com.algorithmlearning.shared.library.DashboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** The mutually exclusive states of the dashboard/home aggregate. */
sealed interface DashboardState {
    data object Loading : DashboardState

    data class Content(val dashboard: Dashboard) : DashboardState

    data class Failed(val error: ApiFailureKind) : DashboardState
}

/**
 * Presentation state holder for the dashboard/home aggregate. It depends only on
 * the shared counts-only [DashboardRepository] and never renders a string.
 */
class DashboardViewModel(
    private val dashboard: DashboardRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun load() {
        _state.value = DashboardState.Loading
        scope.launch {
            try {
                _state.value = DashboardState.Content(dashboard.get())
            } catch (failure: ApiFailure) {
                _state.value = DashboardState.Failed(failure.kind)
            }
        }
    }

    fun refresh() = load()
}
