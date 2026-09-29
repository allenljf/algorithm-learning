package com.algorithmlearning.app.navigation

import com.algorithmlearning.shared.AppDestination
import com.algorithmlearning.shared.Navigator

/** Platform boundary for an addressable app route history. */
interface RouteHistory {
    fun initialDestination(): AppDestination?
    fun replace(destination: AppDestination)
    fun push(destination: AppDestination)
    fun requestBack(): Boolean
    fun start(onPop: (AppDestination) -> Unit)
    fun stop()
}

/** Native targets deliberately retain the shared in-memory navigator only. */
object NoopRouteHistory : RouteHistory {
    override fun initialDestination(): AppDestination? = null
    override fun replace(destination: AppDestination) = Unit
    override fun push(destination: AppDestination) = Unit
    override fun requestBack(): Boolean = false
    override fun start(onPop: (AppDestination) -> Unit) = Unit
    override fun stop() = Unit
}

internal fun pathFor(destination: AppDestination): String = when (destination) {
    AppDestination.Dashboard -> "/"
    AppDestination.Problems -> "/problems"
    AppDestination.Courses -> "/courses"
    is AppDestination.CourseDetail -> "/courses/${destination.sourceIdentity}"
    is AppDestination.ProblemDetail -> "/problems/${destination.problemId}"
    AppDestination.Review -> "/review"
    AppDestination.Settings -> "/settings"
}

internal fun destinationForPath(path: String): AppDestination? {
    val normalized = path.trimEnd('/').ifBlank { "/" }
    return when {
        normalized == "/" -> AppDestination.Dashboard
        normalized == "/problems" -> AppDestination.Problems
        normalized.startsWith("/problems/") -> normalized.removePrefix("/problems/")
            .takeIf(String::isNotBlank)
            ?.let(AppDestination::ProblemDetail)
        normalized == "/courses" -> AppDestination.Courses
        normalized.startsWith("/courses/") -> normalized.removePrefix("/courses/")
            .takeIf(String::isNotBlank)
            ?.let(AppDestination::CourseDetail)
        normalized == "/review" -> AppDestination.Review
        normalized == "/settings" -> AppDestination.Settings
        else -> null
    }
}

/**
 * Reconciles platform history with the shared navigator while suppressing the
 * inevitable navigator emission caused by a browser popstate callback.
 */
class RouteHistoryCoordinator(
    private val navigator: Navigator,
    private val history: RouteHistory,
) {
    private var started = false
    private var routePublished = false
    private var awaitingPopDestination: AppDestination? = null

    fun start() {
        if (started) return
        started = true
        history.start { destination ->
            awaitingPopDestination = destination
            navigator.replaceWith(destination)
        }
    }

    fun stop() {
        if (!started) return
        history.stop()
        started = false
        awaitingPopDestination = null
        routePublished = false
    }

    fun navigatorChanged(destination: AppDestination) {
        if (awaitingPopDestination == destination) {
            awaitingPopDestination = null
            return
        }
        if (!started) return
        if (!routePublished) history.replace(destination) else history.push(destination)
        routePublished = true
    }

    fun appBack(): Boolean = history.requestBack() || navigator.goBack()
}
