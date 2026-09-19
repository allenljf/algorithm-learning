package com.algorithmlearning.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The top-level destinations of the client. Kept transport-agnostic so the
 * data and UI layers can grow underneath it without a navigation library.
 */
sealed interface AppDestination {
    data object Dashboard : AppDestination
    data object Problems : AppDestination
    data object Review : AppDestination
    data object Settings : AppDestination
}

/**
 * A small, testable back-stack holder. State is exposed as an immutable list so
 * Compose can collect it and no screen owns its own history.
 */
class Navigator(start: AppDestination = AppDestination.Dashboard) {
    private val _backStack = MutableStateFlow(listOf(start))
    val backStack: StateFlow<List<AppDestination>> = _backStack.asStateFlow()

    val current: AppDestination get() = _backStack.value.last()
    val canGoBack: Boolean get() = _backStack.value.size > 1
    val depth: Int get() = _backStack.value.size

    fun navigateTo(destination: AppDestination) {
        if (destination == current) return
        _backStack.value = _backStack.value + destination
    }

    fun goBack(): Boolean {
        val stack = _backStack.value
        if (stack.size <= 1) return false
        _backStack.value = stack.dropLast(1)
        return true
    }

    fun resetTo(destination: AppDestination) {
        _backStack.value = listOf(destination)
    }
}
