package com.algorithmlearning.shared.auth.data

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Default API origin for local development; injected at the composition root. */
const val DEFAULT_API_BASE_URL: String = "http://localhost:8080"

internal val ApiJson: Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

/**
 * Shared Ktor configuration for every client the app builds. Platform actuals
 * supply the engine and any platform-only plugins.
 */
fun HttpClientConfig<*>.applyAuthDefaults() {
    install(ContentNegotiation) {
        json(ApiJson)
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 15_000
    }
}

/** Builds the platform HTTP client; Android uses OkHttp, Web uses the browser fetch. */
expect fun createAuthHttpClient(): HttpClient
