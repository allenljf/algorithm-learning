@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.algorithmlearning.app.auth

import com.algorithmlearning.app.FakeAuthRepository
import com.algorithmlearning.app.authSession
import com.algorithmlearning.shared.auth.AuthFailure
import com.algorithmlearning.shared.auth.AuthFailureKind
import com.algorithmlearning.shared.auth.AuthSessionHolder
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthViewModelTest {

    @Test
    fun submittingBlankCredentialsIsRejectedWithoutCallingTheRepository() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(AuthSessionHolder(repository), backgroundScope)

        viewModel.submit()
        advanceUntilIdle()

        assertEquals(AuthErrorKind.MISSING_CREDENTIALS, viewModel.form.value.error)
        assertTrue(repository.loginCalls.isEmpty())
    }

    @Test
    fun successfulLoginPublishesTheSession() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeAuthRepository()
        val holder = AuthSessionHolder(repository)
        val viewModel = AuthViewModel(holder, backgroundScope)

        viewModel.emailChanged("a@test.dev")
        viewModel.passwordChanged("password")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals("a@test.dev", holder.state.value.session?.user?.email)
        assertNull(viewModel.form.value.error)
        assertFalse(viewModel.form.value.submitting)
        assertEquals(listOf("a@test.dev" to "password"), repository.loginCalls)
    }

    @Test
    fun invalidCredentialsSurfaceATypedErrorAndKeepTheDraft() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeAuthRepository(
            loginHandler = { _, _ -> throw AuthFailure(AuthFailureKind.INVALID_CREDENTIALS) },
        )
        val viewModel = AuthViewModel(AuthSessionHolder(repository), backgroundScope)

        viewModel.emailChanged("a@test.dev")
        viewModel.passwordChanged("wrong-password")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals(AuthErrorKind.INVALID_CREDENTIALS, viewModel.form.value.error)
        assertEquals("a@test.dev", viewModel.form.value.email)
        assertEquals("wrong-password", viewModel.form.value.password)
        assertFalse(viewModel.form.value.submitting)
    }

    @Test
    fun rateLimitingMapsToItsOwnErrorKind() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeAuthRepository(
            registerHandler = { _, _ -> throw AuthFailure(AuthFailureKind.RATE_LIMITED) },
        )
        val viewModel = AuthViewModel(AuthSessionHolder(repository), backgroundScope)

        viewModel.toggleMode()
        viewModel.emailChanged("a@test.dev")
        viewModel.passwordChanged("password")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals(AuthErrorKind.RATE_LIMITED, viewModel.form.value.error)
        assertEquals(listOf("a@test.dev" to "password"), repository.registerCalls)
    }

    @Test
    fun logoutClearsTheSession() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeAuthRepository(restoreHandler = { authSession("a@test.dev") })
        val holder = AuthSessionHolder(repository)
        val viewModel = AuthViewModel(holder, backgroundScope)

        holder.restore()
        advanceUntilIdle()
        assertTrue(holder.state.value.authenticated)

        viewModel.logout()
        advanceUntilIdle()

        assertFalse(holder.state.value.authenticated)
        assertEquals(1, repository.logoutCount)
    }
}
