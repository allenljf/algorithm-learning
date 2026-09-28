@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.algorithmlearning.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.algorithmlearning.app.auth.AuthFormState
import com.algorithmlearning.app.auth.AuthMode
import com.algorithmlearning.app.auth.AuthScreen
import com.algorithmlearning.app.auth.composePixelsToCssPixels
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.StringCatalog
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.events.Event
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@JsFun("() => new Event('input', { bubbles: true })")
private external fun nativeInputEvent(): Event

@OptIn(ExperimentalTestApi::class)
class NativeAuthFieldsUiTest {

    @Test
    fun composePixelGeometryConvertsToCssPixelsAtHighDensity() {
        assertEquals(430.0, composePixelsToCssPixels(860f, 2.0))
        assertEquals(420.0, composePixelsToCssPixels(840f, 2.0))
        assertEquals(56.0, composePixelsToCssPixels(112f, 2.0))
    }

    @AfterTest
    fun removeComposeTarget() {
        document.getElementById("composeTarget")?.remove()
    }

    @Test
    fun loginRendersNativeEmailAndPasswordInputs() = runComposeUiTest {
        createComposeTarget()
        setContent {
            AuthScreen(
                state = AuthFormState(),
                strings = StringCatalog.of(AppLanguage.ENGLISH),
                onEmailChange = {},
                onPasswordChange = {},
                onSubmit = {},
                onToggleMode = {},
            )
        }

        val email = document.querySelector("input[data-auth-native-input='email']") as? HTMLInputElement
        val password = document.querySelector("input[data-auth-native-input='password']") as? HTMLInputElement

        assertNotNull(email)
        assertNotNull(password)
        assertEquals("email", email.type)
        assertEquals("username", email.autocomplete)
        assertEquals("password", password.type)
        assertEquals("current-password", password.autocomplete)
    }

    @Test
    fun registerModeAndSubmittingStateUpdateNativeInputSemantics() = runComposeUiTest {
        createComposeTarget()
        setContent {
            AuthScreen(
                state = AuthFormState(mode = AuthMode.REGISTER, submitting = true),
                strings = StringCatalog.of(AppLanguage.ENGLISH),
                onEmailChange = {},
                onPasswordChange = {},
                onSubmit = {},
                onToggleMode = {},
            )
        }

        val email = nativeInput("email")
        val password = nativeInput("password")
        assertEquals("new-password", password.autocomplete)
        assertEquals(true, email.disabled)
        assertEquals(true, password.disabled)
    }

    @Test
    fun nativeInputEventRetainsFocusedValueAfterCallbackRecomposition() = runComposeUiTest {
        createComposeTarget()
        var state by mutableStateOf(AuthFormState())
        setContent {
            AuthScreen(
                state = state,
                strings = StringCatalog.of(AppLanguage.ENGLISH),
                onEmailChange = { state = state.copy(email = it) },
                onPasswordChange = {},
                onSubmit = {},
                onToggleMode = {},
            )
        }

        val email = nativeInput("email").apply {
            focus()
            value = "mobile@example.invalid"
            dispatchEvent(nativeInputEvent())
        }
        waitForIdle()
        assertEquals("mobile@example.invalid", state.email)
        assertEquals("mobile@example.invalid", email.value)
        assertEquals(email, document.activeElement)
    }

    @Test
    fun passwordVisibilityToggleRetainsValueAndFocus() = runComposeUiTest {
        createComposeTarget()
        setContent {
            AuthScreen(
                state = AuthFormState(password = "test-password"),
                strings = StringCatalog.of(AppLanguage.ENGLISH),
                onEmailChange = {},
                onPasswordChange = {},
                onSubmit = {},
                onToggleMode = {},
            )
        }

        val password = nativeInput("password").apply { focus() }
        nativeAction("password").click()
        waitForIdle()

        assertEquals("text", password.type)
        assertEquals("test-password", password.value)
        assertEquals(password, document.activeElement)
    }

    @Test
    fun removingAuthScreenRemovesNativeInputs() = runComposeUiTest {
        createComposeTarget()
        var showAuth by mutableStateOf(true)
        setContent {
            if (showAuth) {
                AuthScreen(
                    state = AuthFormState(),
                    strings = StringCatalog.of(AppLanguage.ENGLISH),
                    onEmailChange = {},
                    onPasswordChange = {},
                    onSubmit = {},
                    onToggleMode = {},
                )
            }
        }

        assertNotNull(document.querySelector("input[data-auth-native-input='email']"))
        showAuth = false
        waitForIdle()
        assertEquals(null, document.querySelector("input[data-auth-native-input='email']"))
    }

    private fun createComposeTarget() {
        document.getElementById("composeTarget")?.remove()
        document.body!!.appendChild(document.createElement("div").apply { id = "composeTarget" })
    }

    private fun nativeInput(kind: String): HTMLInputElement = assertNotNull(
        document.querySelector("input[data-auth-native-input='$kind']") as? HTMLInputElement,
    )

    private fun nativeAction(kind: String): HTMLButtonElement = assertNotNull(
        document.querySelector("button[data-auth-native-input-action='$kind']") as? HTMLButtonElement,
    )
}
