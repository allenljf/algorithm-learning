package com.algorithmlearning.app.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorithmlearning.app.label
import com.algorithmlearning.shared.AppStrings

/**
 * The unauthenticated gate: a single form that switches between sign-in and
 * registration. It renders [AuthFormState] and emits events upward; it never
 * touches a repository.
 */
@Composable
fun AuthScreen(
    state: AuthFormState,
    strings: AppStrings,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val title = if (state.mode == AuthMode.LOGIN) strings.authLoginTitle else strings.authRegisterTitle
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text(strings.authEmailLabel) },
            singleLine = true,
            enabled = !state.submitting,
            modifier = Modifier
                .padding(top = 24.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .testTag("auth-email"),
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text(strings.authPasswordLabel) },
            singleLine = true,
            enabled = !state.submitting,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) {
                            strings.authHidePasswordAction
                        } else {
                            strings.authShowPasswordAction
                        },
                    )
                }
            },
            modifier = Modifier
                .padding(top = 12.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .testTag("auth-password"),
        )
        state.error?.let { error ->
            Text(
                text = error.label(strings),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp).widthIn(max = 420.dp),
            )
        }
        val submitLabel = if (state.mode == AuthMode.LOGIN) strings.authSignInAction else strings.authCreateAccountAction
        Button(
            onClick = onSubmit,
            enabled = !state.submitting,
            modifier = Modifier
                .padding(top = 20.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .testTag("auth-submit"),
        ) {
            Text(submitLabel)
        }
        TextButton(
            onClick = onToggleMode,
            enabled = !state.submitting,
            modifier = Modifier.padding(top = 4.dp),
        ) {
            Text(
                text = if (state.mode == AuthMode.LOGIN) {
                    strings.authSwitchToRegisterAction
                } else {
                    strings.authSwitchToLoginAction
                },
            )
        }
    }
}
