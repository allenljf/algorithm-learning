package com.algorithmlearning.shared.auth

/**
 * The transport contract for the frozen `/api/v1/auth` routes. Implementations
 * adapt a concrete HTTP client; callers only ever see domain models and
 * [AuthFailure].
 *
 * The refresh token is an HttpOnly cookie owned by the platform client, so it is
 * never a parameter or return value here. Access tokens stay in memory.
 */
interface AuthRemote {
    suspend fun register(email: String, password: String): AuthSession

    suspend fun login(email: String, password: String): AuthSession

    /** Rotates the refresh cookie and returns a fresh access token. */
    suspend fun refresh(): AccessToken

    /** Resolves the authenticated identity behind [accessToken] via `GET /auth/me`. */
    suspend fun currentUser(accessToken: String): AuthUser

    suspend fun logout()
}
