package com.algorithmlearning.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.algorithmlearning.app.auth.AuthErrorKind
import com.algorithmlearning.app.auth.AuthFormState
import com.algorithmlearning.app.auth.AuthMode
import com.algorithmlearning.app.auth.AuthScreen
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.StringCatalog
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AuthScreenUiTest {

    private val strings = StringCatalog.of(AppLanguage.ENGLISH)

    @Test
    fun loginSubmitsTheTypedCredentials() = runComposeUiTest {
        var submitted = 0
        setContent {
            AuthScreen(
                state = AuthFormState(),
                strings = strings,
                onEmailChange = {},
                onPasswordChange = {},
                onSubmit = { submitted++ },
                onToggleMode = {},
            )
        }
        onNodeWithTag("auth-email").assertIsDisplayed()
        onNodeWithTag("auth-password").assertIsDisplayed()
        onNodeWithTag("auth-submit").performClick()
        assertEquals(1, submitted)
    }

    @Test
    fun registerModeAndErrorsAreRenderedFromState() = runComposeUiTest {
        var toggled = 0
        setContent {
            AuthScreen(
                state = AuthFormState(mode = AuthMode.REGISTER, error = AuthErrorKind.RATE_LIMITED),
                strings = strings,
                onEmailChange = {},
                onPasswordChange = {},
                onSubmit = {},
                onToggleMode = { toggled++ },
            )
        }
        onNodeWithText(strings.authRateLimitedMessage).assertIsDisplayed()
        onNodeWithText(strings.authSwitchToLoginAction).assertIsDisplayed()
        onNodeWithText(strings.authSwitchToLoginAction).performClick()
        assertEquals(1, toggled)
    }
}
