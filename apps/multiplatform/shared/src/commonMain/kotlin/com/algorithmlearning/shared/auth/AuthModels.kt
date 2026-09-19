package com.algorithmlearning.shared.auth

import kotlin.time.Instant

/**
 * An authenticated identity. Mirrors the frozen `GET /auth/me` payload.
 */
data class AuthUser(
    val id: String,
    val email: String,
)

/**
 * A live session: the identity plus the in-memory access token and its expiry.
 * The refresh token never appears here; it travels only as an HTTP cookie.
 */
data class AuthSession(
    val user: AuthUser,
    val accessToken: String,
    val expiresAt: Instant,
)

/**
 * A rotated access token returned by `POST /auth/refresh`, which does not repeat
 * the user payload.
 */
data class AccessToken(
    val value: String,
    val expiresAt: Instant,
)

/**
 * Immutable auth state for the UI. [loading] marks an in-flight restore so the
 * UI can render a loading branch without reading a repository.
 */
data class AuthState(
    val session: AuthSession? = null,
    val loading: Boolean = false,
) {
    val authenticated: Boolean get() = session != null

    fun copyWith(
        session: AuthSession? = this.session,
        loading: Boolean = this.loading,
        clear: Boolean = false,
    ): AuthState = AuthState(
        session = if (clear) null else session,
        loading = loading,
    )
}
