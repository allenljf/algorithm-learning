package com.algorithmlearning.shared.auth

import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Instant

/**
 * Scriptable [AuthRemote] used by data-flow tests. It records every call so the
 * single-flight and session behavior can be asserted without a network.
 */
class FakeAuthRemote : AuthRemote {
    var user: AuthUser = AuthUser(id = "user-1", email = "a@test.dev")
    var token: String = "access-1"
    var expiresAt: Instant = Instant.parse("2026-01-01T00:15:00Z")

    var loginResult: AuthSession? = null
    var registerResult: AuthSession? = null

    var loginFailure: AuthFailure? = null
    var registerFailure: AuthFailure? = null
    var refreshFailure: AuthFailure? = null
    var currentUserFailure: AuthFailure? = null
    var logoutFailure: AuthFailure? = null

    /** When set, [refresh] suspends until the gate is completed. */
    var refreshGate: CompletableDeferred<Unit>? = null

    var loginCalls: Int = 0
    var registerCalls: Int = 0
    var refreshCalls: Int = 0
    var currentUserCalls: Int = 0
    var logoutCalls: Int = 0

    override suspend fun register(email: String, password: String): AuthSession {
        registerCalls++
        registerFailure?.let { throw it }
        return registerResult ?: AuthSession(user, token, expiresAt)
    }

    override suspend fun login(email: String, password: String): AuthSession {
        loginCalls++
        loginFailure?.let { throw it }
        return loginResult ?: AuthSession(user, token, expiresAt)
    }

    override suspend fun refresh(): AccessToken {
        refreshCalls++
        refreshGate?.await()
        refreshFailure?.let { throw it }
        return AccessToken(token, expiresAt)
    }

    override suspend fun currentUser(accessToken: String): AuthUser {
        currentUserCalls++
        currentUserFailure?.let { throw it }
        return user
    }

    override suspend fun logout() {
        logoutCalls++
        logoutFailure?.let { throw it }
    }
}
