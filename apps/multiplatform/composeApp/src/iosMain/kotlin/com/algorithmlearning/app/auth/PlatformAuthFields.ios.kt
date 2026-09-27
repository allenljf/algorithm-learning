package com.algorithmlearning.app.auth

import androidx.compose.runtime.Composable
import com.algorithmlearning.shared.AppStrings

@Composable
internal actual fun PlatformAuthFields(
    state: AuthFormState,
    strings: AppStrings,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) = ComposeAuthFields(state, strings, onEmailChange, onPasswordChange)
