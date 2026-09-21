package com.algorithmlearning.shared

/** Non-secret persistence boundary supplied by each platform entry point. */
interface EndpointOverrideStore {
    fun read(): String?
    fun write(value: String?)
}

class InvalidApiBaseUrl : IllegalArgumentException()

/** Validates and owns the selected API origin; UI never accesses platform storage. */
class EndpointSettings(
    val buildDefault: String,
    private val store: EndpointOverrideStore,
) {
    private var override: String? = store.read()?.let(::normalize)

    val effectiveBaseUrl: String
        get() = override ?: buildDefault

    fun save(candidate: String) {
        override = normalize(candidate)
        store.write(override)
    }

    fun reset() {
        override = null
        store.write(null)
    }

    private fun normalize(value: String): String {
        val trimmed = value.trim().trimEnd('/')
        val schemeEnd = trimmed.indexOf("://")
        if (schemeEnd <= 0 || trimmed.isEmpty()) throw InvalidApiBaseUrl()
        val scheme = trimmed.substring(0, schemeEnd).lowercase()
        val authority = trimmed.substring(schemeEnd + 3)
        if (scheme !in setOf("http", "https") || authority.isBlank() ||
            authority.contains('/') || authority.contains('?') || authority.contains('#')) {
            throw InvalidApiBaseUrl()
        }
        return "$scheme://$authority"
    }
}
