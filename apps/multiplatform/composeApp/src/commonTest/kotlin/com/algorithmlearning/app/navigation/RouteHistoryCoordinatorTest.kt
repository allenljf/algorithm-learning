package com.algorithmlearning.app.navigation

import com.algorithmlearning.shared.AppDestination
import com.algorithmlearning.shared.Navigator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RouteHistoryCoordinatorTest {
    @Test
    fun firstRouteReplacesThenDetailNavigationPushesACanonicalUrl() {
        val history = FakeRouteHistory()
        val coordinator = RouteHistoryCoordinator(Navigator(AppDestination.Problems), history)

        coordinator.start()
        coordinator.navigatorChanged(AppDestination.Problems)
        coordinator.navigatorChanged(AppDestination.ProblemDetail("two-sum"))

        assertEquals(listOf("replace:/problems", "push:/problems/two-sum"), history.operations)
    }

    @Test
    fun browserPopReconcilesNavigatorWithoutAnEchoPush() {
        val navigator = Navigator(AppDestination.Problems)
        val history = FakeRouteHistory()
        val coordinator = RouteHistoryCoordinator(navigator, history)
        coordinator.start()
        coordinator.navigatorChanged(navigator.current)
        navigator.navigateTo(AppDestination.ProblemDetail("two-sum"))
        coordinator.navigatorChanged(navigator.current)

        history.pop(AppDestination.Problems)
        coordinator.navigatorChanged(navigator.current)

        assertEquals(AppDestination.Problems, navigator.current)
        assertEquals(listOf("replace:/problems", "push:/problems/two-sum"), history.operations)
    }

    @Test
    fun appBackDelegatesToBrowserOnceWhenItHasHistory() {
        val history = FakeRouteHistory(backResult = true)
        val coordinator = RouteHistoryCoordinator(Navigator(AppDestination.ProblemDetail("two-sum")), history)

        assertTrue(coordinator.appBack())
        assertEquals(listOf("back"), history.operations)
    }

    @Test
    fun unknownPathsDoNotBecomeDestinations() {
        assertEquals(AppDestination.ProblemDetail("two-sum"), destinationForPath("/problems/two-sum"))
        assertEquals(null, destinationForPath("/not-a-route"))
        assertFalse(pathFor(AppDestination.ProblemDetail("two-sum")).isBlank())
    }
}

private class FakeRouteHistory(private val backResult: Boolean = false) : RouteHistory {
    val operations = mutableListOf<String>()
    private var onPop: ((AppDestination) -> Unit)? = null

    override fun initialDestination(): AppDestination? = null
    override fun replace(destination: AppDestination) { operations += "replace:${pathFor(destination)}" }
    override fun push(destination: AppDestination) { operations += "push:${pathFor(destination)}" }
    override fun requestBack(): Boolean { operations += "back"; return backResult }
    override fun start(onPop: (AppDestination) -> Unit) { this.onPop = onPop }
    override fun stop() = Unit
    fun pop(destination: AppDestination) { onPop?.invoke(destination) }
}
