package com.algorithmlearning.app.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.algorithmlearning.shared.AppStrings
import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

// native-auth-input-contract: browser-native credential controls own mobile IME focus.
@Composable
internal actual fun PlatformAuthFields(
    state: AuthFormState,
    strings: AppStrings,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    if (document.getElementById("composeTarget") == null) {
        ComposeAuthFields(state, strings, onEmailChange, onPasswordChange)
        return
    }
    var passwordVisible by remember { mutableStateOf(false) }
    NativeAuthInput(
        kind = "email",
        value = state.email,
        label = strings.authEmailLabel,
        type = "email",
        autocomplete = "username",
        enabled = !state.submitting,
        topPadding = 24.dp,
        onValueChange = onEmailChange,
    )
    NativeAuthInput(
        kind = "password",
        value = state.password,
        label = strings.authPasswordLabel,
        type = if (passwordVisible) "text" else "password",
        autocomplete = if (state.mode == AuthMode.LOGIN) "current-password" else "new-password",
        enabled = !state.submitting,
        topPadding = 12.dp,
        onValueChange = onPasswordChange,
        actionLabel = if (passwordVisible) strings.authHidePasswordAction else strings.authShowPasswordAction,
        onAction = { passwordVisible = !passwordVisible },
    )
}

@Composable
private fun NativeAuthInput(
    kind: String,
    value: String,
    label: String,
    type: String,
    autocomplete: String,
    enabled: Boolean,
    topPadding: Dp,
    onValueChange: (String) -> Unit,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnAction by rememberUpdatedState(onAction)
    val input = remember(kind) {
        (document.createElement("input") as HTMLInputElement).apply {
            className = "native-auth-input"
            setAttribute("data-auth-native-input", kind)
            setAttribute("aria-label", label)
        }
    }
    val action = remember(kind, actionLabel != null) {
        actionLabel?.let {
            (document.createElement("button") as HTMLButtonElement).apply {
                className = "native-auth-input-action"
                this.type = "button"
                setAttribute("data-auth-native-input-action", kind)
            }
        }
    }
    DisposableEffect(input, action) {
        val host = requireNotNull(document.getElementById("composeTarget"))
        val inputHandler: (Event) -> Unit = { currentOnValueChange(input.value) }
        input.addEventListener("input", inputHandler)
        host.appendChild(input)
        action?.let { button ->
            val preserveInputFocus: (Event) -> Unit = { it.preventDefault() }
            val actionHandler: (Event) -> Unit = {
                currentOnAction?.invoke()
                input.focus()
            }
            button.addEventListener("mousedown", preserveInputFocus)
            button.addEventListener("pointerdown", preserveInputFocus)
            button.addEventListener("click", actionHandler)
            host.appendChild(button)
            onDispose {
                input.removeEventListener("input", inputHandler)
                button.removeEventListener("mousedown", preserveInputFocus)
                button.removeEventListener("pointerdown", preserveInputFocus)
                button.removeEventListener("click", actionHandler)
                input.remove()
                button.remove()
            }
        } ?: onDispose {
            input.removeEventListener("input", inputHandler)
            input.remove()
        }
    }
    SideEffect {
        input.type = type
        input.autocomplete = autocomplete
        input.disabled = !enabled
        input.placeholder = label
        input.setAttribute("aria-label", label)
        if (document.activeElement != input && input.value != value) input.value = value
        action?.apply {
            textContent = actionLabel
            disabled = !enabled
            setAttribute("aria-label", actionLabel.orEmpty())
        }
    }
    Box(
        modifier = Modifier
            .padding(top = topPadding)
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .height(56.dp)
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInWindow()
                input.style.left = "${position.x}px"
                input.style.top = "${position.y}px"
                input.style.width = "${coordinates.size.width}px"
                input.style.height = "${coordinates.size.height}px"
                action?.apply {
                    style.right = "${position.x + 12}px"
                    style.top = "${position.y + (coordinates.size.height - 36) / 2}px"
                }
            },
    )
}
