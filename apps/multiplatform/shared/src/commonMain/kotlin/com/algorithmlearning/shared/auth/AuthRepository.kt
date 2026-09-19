package com.algorithmlearning.shared.auth

/**
 * The application-facing auth contract. It owns the in-memory access token and
 * turns the transport contract into a session lifecycle the UI and the other
 * repositories can depend on.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): AuthSession

    suspend fun register(email: String, password: String): AuthSession

    /**
     * Rebuilds a session from a persisted refresh cookie at app start, or returns
     * `null` when no valid session can be restored.
     */
    suspend fun restore(): AuthSession?

    suspend fun logout()

    /**
     * Returns a non-expired access token, refreshing once when needed. Callers
     * attach the result as `Authorization: Bearer <token>`.
     */
    suspend fun accessToken(): String?

    /**
     * Drops the cached access token so the next [accessToken] call refreshes.
     * Used after an authenticated request receives `401`.
     */
    suspend fun invalidateAccessToken()
}
