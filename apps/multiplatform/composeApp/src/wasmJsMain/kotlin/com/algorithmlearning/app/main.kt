package com.algorithmlearning.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.algorithmlearning.shared.EndpointOverrideStore
import kotlinx.browser.localStorage

@JsFun("() => globalThis.__ALGORITHM_LEARNING_API_BASE_URL__")
private external fun configuredApiBaseUrl(): String

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "composeTarget") {
        App(
            defaultApiBaseUrl = configuredApiBaseUrl(),
            endpointStore = object : EndpointOverrideStore {
                override fun read(): String? = localStorage.getItem("api-base-url")
                override fun write(value: String?) { if (value == null) localStorage.removeItem("api-base-url") else localStorage.setItem("api-base-url", value) }
            },
        )
    }
}
