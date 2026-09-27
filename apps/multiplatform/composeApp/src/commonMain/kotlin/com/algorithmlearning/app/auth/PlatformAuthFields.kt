package com.algorithmlearning.app.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.algorithmlearning.shared.AppStrings

@Composable
internal expect fun PlatformAuthFields(
    state: AuthFormState,
    strings: AppStrings,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
)

@Composable
internal fun ComposeAuthFields(
    state: AuthFormState,
    strings: AppStrings,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

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
                    if (passwordVisible) strings.authHidePasswordAction
                    else strings.authShowPasswordAction,
                )
            }
        },
        modifier = Modifier
            .padding(top = 12.dp)
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .testTag("auth-password"),
    )
}
