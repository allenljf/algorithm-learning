package com.algorithmlearning.app.navigation

import com.algorithmlearning.shared.AppDestination
import kotlinx.browser.window
import org.w3c.dom.events.Event

/** Browser implementation; Compose only receives its platform-neutral seam. */
class WasmRouteHistory : RouteHistory {
    private var onPop: ((AppDestination) -> Unit)? = null
    private val popHandler: (Event) -> Unit = {
        destinationForPath(window.location.pathname)?.let { destination -> onPop?.invoke(destination) }
    }

    override fun initialDestination(): AppDestination? = destinationForPath(window.location.pathname)

    override fun replace(destination: AppDestination) {
        window.history.replaceState(null, "", pathFor(destination))
    }

    override fun push(destination: AppDestination) {
        window.history.pushState(null, "", pathFor(destination))
    }

    override fun requestBack(): Boolean {
        if (window.history.length <= 1) return false
        window.history.back()
        return true
    }

    override fun start(onPop: (AppDestination) -> Unit) {
        this.onPop = onPop
        window.addEventListener("popstate", popHandler)
    }

    override fun stop() {
        window.removeEventListener("popstate", popHandler)
        onPop = null
    }
}
