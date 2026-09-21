package com.algorithmlearning.shared.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.cookies.HttpCookies

/**
 * iOS engine. [HttpCookies] keeps refresh cookies with the client instance;
 * rebuilding the app container for a changed endpoint also creates this engine
 * anew, so cookies and in-memory session state are never reused by another
 * origin.
 */
actual fun createAuthHttpClient(): HttpClient = HttpClient(Darwin) {
    applyAuthDefaults()
    install(HttpCookies)
}
