package com.algorithmlearning.app.navigation

import com.algorithmlearning.shared.AppDestination
import com.algorithmlearning.shared.Navigator
import kotlinx.browser.window
import org.w3c.dom.events.Event
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmRouteHistoryTest {
    @Test
    fun popstateRestoresTheDetailRouteWithoutRewritingIt() {
        val originalPath = window.location.pathname
        val navigator = Navigator(AppDestination.Problems)
        val history = WasmRouteHistory()
        val coordinator = RouteHistoryCoordinator(navigator, history)
        try {
            coordinator.start()
            coordinator.navigatorChanged(AppDestination.Problems)
            window.history.replaceState(null, "", "/problems/two-sum")
            window.dispatchEvent(Event("popstate"))

            assertEquals(AppDestination.ProblemDetail("two-sum"), navigator.current)
            coordinator.navigatorChanged(navigator.current)
            assertEquals("/problems/two-sum", window.location.pathname)
        } finally {
            coordinator.stop()
            window.history.replaceState(null, "", originalPath)
        }
    }
}
