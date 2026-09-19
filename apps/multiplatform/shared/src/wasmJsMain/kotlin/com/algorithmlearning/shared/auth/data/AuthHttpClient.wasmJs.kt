package com.algorithmlearning.shared.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

/**
 * Wasm engine. The browser owns the refresh cookie, so no cookie plugin is
 * installed here.
 */
actual fun createAuthHttpClient(): HttpClient = HttpClient(Js) {
    applyAuthDefaults()
}
