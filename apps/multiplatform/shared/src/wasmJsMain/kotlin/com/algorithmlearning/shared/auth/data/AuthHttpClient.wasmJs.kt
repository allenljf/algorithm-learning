package com.algorithmlearning.shared.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.toJsString

/**
 * Wasm engine. The browser owns the refresh cookie, so no cookie plugin is
 * installed here.
 */
@OptIn(ExperimentalWasmJsInterop::class)
actual fun createAuthHttpClient(): HttpClient = HttpClient(Js) {
    applyAuthDefaults()
    engine {
        // A refresh session is an HttpOnly browser cookie. Request it explicitly
        // so a fresh Wasm application instance can restore the signed-in user.
        configureRequest { credentials = "include".toJsString() }
    }
}
