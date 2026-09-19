package com.algorithmlearning.shared.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class AuthModelsTest {

    private val user = AuthUser(id = "user-1", email = "a@test.dev")
    private val session = AuthSession(
        user = user,
        accessToken = "access-1",
        expiresAt = Instant.parse("2026-01-01T00:15:00Z"),
    )

    @Test
    fun defaultStateIsUnauthenticatedAndNotLoading() {
        val state = AuthState()
        assertNull(state.session)
        assertFalse(state.authenticated)
        assertFalse(state.loading)
    }

    @Test
    fun copyWithUpdatesLoadingAndPreservesTheSession() {
        val state = AuthState(session = session)
        val loading = state.copyWith(loading = true)
        assertTrue(loading.loading)
        assertEquals(session, loading.session)
        assertTrue(loading.authenticated)
    }

    @Test
    fun copyWithClearDropsTheSession() {
        val cleared = AuthState(session = session).copyWith(clear = true)
        assertNull(cleared.session)
        assertFalse(cleared.authenticated)
    }

    @Test
    fun sessionValuesAreImmutable() {
        val copy = session.copy()
        assertEquals(session, copy)
        assertNotEquals(session, copy.copy(accessToken = "access-2"))
    }
}
