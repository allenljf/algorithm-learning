package com.algorithmlearning.shared.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.cookies.HttpCookies

/**
 * Android engine. [HttpCookies] stores the refresh cookie for the process so
 * `refresh`/`logout` can present it; persistent secure-storage of cookie bytes
 * remains a separate platform-storage concern.
 */
actual fun createAuthHttpClient(): HttpClient = HttpClient(OkHttp) {
    applyAuthDefaults()
    install(HttpCookies)
}
