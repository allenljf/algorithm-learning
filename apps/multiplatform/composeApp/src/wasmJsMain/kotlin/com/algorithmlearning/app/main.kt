package com.algorithmlearning.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.algorithmlearning.app.navigation.WasmRouteHistory
import com.algorithmlearning.shared.EndpointOverrideStore
import kotlinx.browser.localStorage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.configureWebResources
import kotlin.js.ExperimentalWasmJsInterop

@JsFun("() => globalThis.__ALGORITHM_LEARNING_API_BASE_URL__")
private external fun configuredApiBaseUrl(): String

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalResourceApi::class,
    ExperimentalWasmJsInterop::class,
)
fun main() {
    // Detail URLs are deep links, so relative resource paths would resolve below
    // `/problems/{id}` and return the SPA HTML instead of the bundled CJK font.
    configureWebResources {
        resourcePathMapping { path -> "/$path" }
    }
    ComposeViewport(viewportContainerId = "composeTarget") {
        App(
            defaultApiBaseUrl = configuredApiBaseUrl(),
            endpointStore = object : EndpointOverrideStore {
                override fun read(): String? = localStorage.getItem("api-base-url")
                override fun write(value: String?) { if (value == null) localStorage.removeItem("api-base-url") else localStorage.setItem("api-base-url", value) }
            },
            routeHistory = WasmRouteHistory(),
        )
    }
}
