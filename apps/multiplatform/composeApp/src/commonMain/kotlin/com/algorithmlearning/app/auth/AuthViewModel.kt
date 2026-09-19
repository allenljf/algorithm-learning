package com.algorithmlearning.app.auth

import com.algorithmlearning.shared.auth.AuthFailure
import com.algorithmlearning.shared.auth.AuthFailureKind
import com.algorithmlearning.shared.auth.AuthSessionHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Which credential form the unauthenticated gate is showing. */
enum class AuthMode { LOGIN, REGISTER }

/**
 * The presentation-level reason a credential submission did not produce a
 * session. [AuthFailureKind] values are translated so the screen never depends
 * on the transport type; [MISSING_CREDENTIALS] is a local, pre-flight check.
 */
enum class AuthErrorKind {
    MISSING_CREDENTIALS,
    INVALID_CREDENTIALS,
    RATE_LIMITED,
    NETWORK,
    SERVER,
    UNEXPECTED,
}

/** Immutable form state for the login/register gate. */
data class AuthFormState(
    val mode: AuthMode = AuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val submitting: Boolean = false,
    val error: AuthErrorKind? = null,
)

/**
 * Owns the credential form and delegates the session lifecycle to
 * [AuthSessionHolder]. It never touches an HTTP type and never renders a
 * string; the screen maps [AuthErrorKind] through the string catalog.
 */
class AuthViewModel(
    private val session: AuthSessionHolder,
    private val scope: CoroutineScope,
) {
    private val _form = MutableStateFlow(AuthFormState())
    val form: StateFlow<AuthFormState> = _form.asStateFlow()

    fun emailChanged(value: String) = _form.update { it.copy(email = value, error = null) }

    fun passwordChanged(value: String) = _form.update { it.copy(password = value, error = null) }

    fun toggleMode() = _form.update {
        it.copy(
            mode = if (it.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN,
            error = null,
        )
    }

    fun submit() {
        val values = _form.value
        if (values.submitting) return
        if (values.email.isBlank() || values.password.isBlank()) {
            _form.update { it.copy(error = AuthErrorKind.MISSING_CREDENTIALS) }
            return
        }
        _form.update { it.copy(submitting = true, error = null) }
        scope.launch {
            try {
                if (values.mode == AuthMode.LOGIN) {
                    session.login(values.email.trim(), values.password)
                } else {
                    session.register(values.email.trim(), values.password)
                }
                _form.value = AuthFormState(mode = values.mode)
            } catch (failure: AuthFailure) {
                _form.update { it.copy(submitting = false, error = failure.kind.toErrorKind()) }
            }
        }
    }

    fun logout() {
        scope.launch { session.logout() }
    }
}

private fun AuthFailureKind.toErrorKind(): AuthErrorKind = when (this) {
    AuthFailureKind.INVALID_CREDENTIALS -> AuthErrorKind.INVALID_CREDENTIALS
    AuthFailureKind.RATE_LIMITED -> AuthErrorKind.RATE_LIMITED
    AuthFailureKind.NETWORK -> AuthErrorKind.NETWORK
    AuthFailureKind.SERVER -> AuthErrorKind.SERVER
    AuthFailureKind.UNAUTHORIZED, AuthFailureKind.UNEXPECTED -> AuthErrorKind.UNEXPECTED
}
