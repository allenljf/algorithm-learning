package com.algorithmlearning.shared.auth

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private class FixedClock(var current: Instant) : Clock {
    override fun now(): Instant = current
}

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteAuthRepositoryTest {

    private val base = Instant.parse("2026-01-01T00:00:00Z")

    private fun repository(remote: FakeAuthRemote, now: Instant = base): RemoteAuthRepository =
        RemoteAuthRepository(remote = remote, clock = FixedClock(now))

    @Test
    fun loginStoresSessionAndAccessToken() = runTest {
        val remote = FakeAuthRemote().apply { expiresAt = base + 15.minutes }
        val repository = repository(remote)

        val session = repository.login("a@test.dev", "password1234")

        assertEquals("access-1", session.accessToken)
        assertEquals("access-1", repository.accessToken())
        assertEquals(0, remote.refreshCalls)
    }

    @Test
    fun loginFailurePropagatesAsAuthFailure() = runTest {
        val remote = FakeAuthRemote().apply {
            loginFailure = AuthFailure(AuthFailureKind.INVALID_CREDENTIALS)
        }
        val repository = repository(remote)

        val failure = assertFailsWith<AuthFailure> {
            repository.login("a@test.dev", "password1234")
        }
        assertEquals(AuthFailureKind.INVALID_CREDENTIALS, failure.kind)
    }

    @Test
    fun expiredTokenRefreshesOnceThenIsReused() = runTest {
        val remote = FakeAuthRemote().apply { expiresAt = base + 5.minutes }
        val clock = FixedClock(base)
        val repository = RemoteAuthRepository(remote, clock)

        repository.login("a@test.dev", "password1234")
        assertEquals("access-1", repository.accessToken())
        assertEquals(0, remote.refreshCalls)

        clock.current = base + 10.minutes
        remote.token = "access-2"
        remote.expiresAt = base + 20.minutes

        assertEquals("access-2", repository.accessToken())
        assertEquals(1, remote.refreshCalls)
        assertEquals("access-2", repository.accessToken())
        assertEquals(1, remote.refreshCalls)
    }

    @Test
    fun concurrentExpiredCallsRefreshExactlyOnce() = runTest {
        val gate = CompletableDeferred<Unit>()
        val remote = FakeAuthRemote().apply {
            expiresAt = base - 1.minutes
            refreshGate = gate
        }
        val repository = repository(remote)
        repository.login("a@test.dev", "password1234")

        val callers = (1..5).map { async { repository.accessToken() } }
        runCurrent()
        remote.token = "access-refreshed"
        remote.expiresAt = base + 15.minutes
        gate.complete(Unit)

        val tokens = callers.awaitAll()
        assertEquals(1, remote.refreshCalls)
        assertEquals(List(5) { "access-refreshed" }, tokens)
    }

    @Test
    fun refreshFailureClearsTheAccessToken() = runTest {
        val remote = FakeAuthRemote().apply {
            expiresAt = base - 1.minutes
            refreshFailure = AuthFailure(AuthFailureKind.UNAUTHORIZED)
        }
        val repository = repository(remote)
        repository.login("a@test.dev", "password1234")

        assertNull(repository.accessToken())
        assertNull(repository.accessToken())
    }

    @Test
    fun invalidateForcesTheNextRefresh() = runTest {
        val remote = FakeAuthRemote().apply { expiresAt = base + 15.minutes }
        val repository = repository(remote)
        repository.login("a@test.dev", "password1234")

        repository.invalidateAccessToken()
        repository.accessToken()

        assertEquals(1, remote.refreshCalls)
    }

    @Test
    fun restoreBuildsASessionFromRefreshedTokenAndCurrentUser() = runTest {
        val remote = FakeAuthRemote().apply { expiresAt = base + 15.minutes }
        val repository = repository(remote)

        val restored = assertNotNull(repository.restore())

        assertEquals("access-1", restored.accessToken)
        assertEquals("user-1", restored.user.id)
        assertEquals("a@test.dev", restored.user.email)
        assertEquals(1, remote.refreshCalls)
        assertEquals(1, remote.currentUserCalls)
    }

    @Test
    fun restoreReturnsNullWhenRefreshFails() = runTest {
        val remote = FakeAuthRemote().apply {
            refreshFailure = AuthFailure(AuthFailureKind.UNAUTHORIZED)
        }
        val repository = repository(remote)

        assertNull(repository.restore())
        assertEquals(0, remote.currentUserCalls)
    }

    @Test
    fun restoreReturnsNullWhenCurrentUserFails() = runTest {
        val remote = FakeAuthRemote().apply {
            expiresAt = base + 15.minutes
            currentUserFailure = AuthFailure(AuthFailureKind.UNAUTHORIZED)
        }
        val repository = repository(remote)

        assertNull(repository.restore())
    }

    @Test
    fun logoutRevokesRemoteAndClearsTheLocalSession() = runTest {
        val remote = FakeAuthRemote().apply { expiresAt = base + 15.minutes }
        val repository = repository(remote)
        repository.login("a@test.dev", "password1234")

        repository.logout()

        assertEquals(1, remote.logoutCalls)
        remote.refreshFailure = AuthFailure(AuthFailureKind.UNAUTHORIZED)
        assertNull(repository.accessToken())
    }
}
