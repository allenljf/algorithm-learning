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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class AuthSessionHolderTest {

    private val base = Instant.parse("2026-01-01T00:00:00Z")

    private fun holder(remote: FakeAuthRemote): AuthSessionHolder =
        AuthSessionHolder(RemoteAuthRepository(remote, Clock.System))

    @Test
    fun loginPublishesAnAuthenticatedState() = runTest {
        val holder = holder(FakeAuthRemote().apply { expiresAt = base + 15.minutes })
        assertFalse(holder.state.value.authenticated)

        val session = holder.login("a@test.dev", "password1234")

        assertTrue(holder.state.value.authenticated)
        assertEquals(session, holder.state.value.session)
        assertFalse(holder.state.value.loading)
    }

    @Test
    fun loginFailureLeavesStateUnauthenticatedAndRethrows() = runTest {
        val remote = FakeAuthRemote().apply {
            loginFailure = AuthFailure(AuthFailureKind.INVALID_CREDENTIALS)
        }
        val holder = holder(remote)

        assertFailsWith<AuthFailure> { holder.login("a@test.dev", "password1234") }

        assertFalse(holder.state.value.authenticated)
        assertFalse(holder.state.value.loading)
    }

    @Test
    fun registerPublishesAnAuthenticatedState() = runTest {
        val holder = holder(FakeAuthRemote().apply { expiresAt = base + 15.minutes })

        val session = holder.register("new@test.dev", "password1234")

        assertTrue(holder.state.value.authenticated)
        assertEquals(session, holder.state.value.session)
    }

    @Test
    fun concurrentRestoresCoalesceIntoOneRefresh() = runTest {
        val gate = CompletableDeferred<Unit>()
        val remote = FakeAuthRemote().apply {
            expiresAt = base + 15.minutes
            refreshGate = gate
        }
        val holder = holder(remote)

        val first = async { holder.restore() }
        val second = async { holder.restore() }
        runCurrent()
        gate.complete(Unit)
        awaitAll(first, second)

        assertEquals(1, remote.refreshCalls)
        assertEquals(1, remote.currentUserCalls)
        assertTrue(holder.state.value.authenticated)
        assertFalse(holder.state.value.loading)
    }

    @Test
    fun restoreWithoutASessionLeavesUnauthenticatedState() = runTest {
        val remote = FakeAuthRemote().apply {
            refreshFailure = AuthFailure(AuthFailureKind.UNAUTHORIZED)
        }
        val holder = holder(remote)

        holder.restore()

        assertFalse(holder.state.value.authenticated)
        assertFalse(holder.state.value.loading)
    }

    @Test
    fun logoutClearsTheSession() = runTest {
        val remote = FakeAuthRemote().apply { expiresAt = base + 15.minutes }
        val holder = holder(remote)
        holder.login("a@test.dev", "password1234")

        holder.logout()

        assertFalse(holder.state.value.authenticated)
        assertNull(holder.state.value.session)
        assertEquals(1, remote.logoutCalls)
    }
}
