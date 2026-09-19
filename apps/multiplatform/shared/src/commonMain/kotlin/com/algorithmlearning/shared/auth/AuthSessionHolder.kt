package com.algorithmlearning.shared.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Publishes auth state for the UI as an immutable [AuthState] flow. It delegates
 * all session work to [AuthRepository] and coalesces concurrent [restore] calls
 * so an app start refreshes the session at most once.
 */
class AuthSessionHolder(private val repository: AuthRepository) {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val restoreMutex = Mutex()
    private var restored = false

    suspend fun restore() {
        restoreMutex.withLock {
            if (restored) return
            _state.value = _state.value.copyWith(loading = true)
            val session = try {
                repository.restore()
            } catch (failure: AuthFailure) {
                _state.value = AuthState(loading = false)
                return
            }
            _state.value = AuthState(session = session, loading = false)
            restored = session != null
        }
    }

    suspend fun login(email: String, password: String): AuthSession =
        authenticate { repository.login(email, password) }

    suspend fun register(email: String, password: String): AuthSession =
        authenticate { repository.register(email, password) }

    suspend fun logout() {
        try {
            repository.logout()
        } finally {
            restored = false
            _state.value = AuthState()
        }
    }

    private suspend fun authenticate(block: suspend () -> AuthSession): AuthSession {
        _state.value = _state.value.copyWith(loading = true)
        return try {
            block().also {
                restored = true
                _state.value = AuthState(session = it)
            }
        } catch (failure: AuthFailure) {
            _state.value = _state.value.copyWith(loading = false)
            throw failure
        }
    }
}
