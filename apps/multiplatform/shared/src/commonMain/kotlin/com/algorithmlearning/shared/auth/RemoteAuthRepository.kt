package com.algorithmlearning.shared.auth

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Default [AuthRepository]. It keeps the access token in memory and serializes
 * refresh behind [tokenMutex] so concurrent `401` handling cannot trigger
 * competing rotations of the refresh cookie: the first caller refreshes, the
 * rest observe the fresh token and reuse it.
 *
 * [expirySkew] lets callers treat a token that is about to expire as expired, so
 * a request is not sent with a token that dies in flight.
 */
class RemoteAuthRepository(
    private val remote: AuthRemote,
    private val clock: Clock = Clock.System,
    private val expirySkew: Duration = Duration.ZERO,
) : AuthRepository {

    private val tokenMutex = Mutex()
    private var session: AuthSession? = null
    private var accessToken: AccessToken? = null

    override suspend fun login(email: String, password: String): AuthSession =
        store(remote.login(email, password))

    override suspend fun register(email: String, password: String): AuthSession =
        store(remote.register(email, password))

    override suspend fun restore(): AuthSession? {
        val token = validAccessToken() ?: return null
        val user = try {
            remote.currentUser(token.value)
        } catch (failure: AuthFailure) {
            clearSession()
            return null
        }
        return store(AuthSession(user = user, accessToken = token.value, expiresAt = token.expiresAt))
    }

    override suspend fun logout() {
        try {
            remote.logout()
        } finally {
            clearSession()
        }
    }

    override suspend fun accessToken(): String? = validAccessToken()?.value

    override suspend fun invalidateAccessToken() {
        tokenMutex.withLock { accessToken = null }
    }

    private suspend fun validAccessToken(): AccessToken? {
        val cached = accessToken
        if (cached != null && !isExpired(cached.expiresAt)) return cached
        return tokenMutex.withLock {
            val fresh = accessToken
            if (fresh != null && !isExpired(fresh.expiresAt)) {
                fresh
            } else {
                val rotated = try {
                    remote.refresh()
                } catch (failure: AuthFailure) {
                    clearSessionLocked()
                    return@withLock null
                }
                accessToken = rotated
                session = session?.copy(accessToken = rotated.value, expiresAt = rotated.expiresAt)
                rotated
            }
        }
    }

    private suspend fun store(next: AuthSession): AuthSession = tokenMutex.withLock {
        session = next
        accessToken = AccessToken(next.accessToken, next.expiresAt)
        next
    }

    private suspend fun clearSession() {
        tokenMutex.withLock { clearSessionLocked() }
    }

    private fun clearSessionLocked() {
        session = null
        accessToken = null
    }

    private fun isExpired(expiresAt: Instant): Boolean =
        expiresAt - expirySkew <= clock.now()
}
