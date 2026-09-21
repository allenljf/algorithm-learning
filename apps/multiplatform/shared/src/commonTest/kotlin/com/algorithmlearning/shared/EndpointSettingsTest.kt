package com.algorithmlearning.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EndpointSettingsTest {
    @Test
    fun normalizesAndPersistsAnAbsoluteOrigin() {
        val store = FakeEndpointStore()
        val settings = EndpointSettings("https://api.example.test", store)

        settings.save("https://staging.example.test/")

        assertEquals("https://staging.example.test", settings.effectiveBaseUrl)
        assertEquals("https://staging.example.test", store.value)
    }

    @Test
    fun rejectsBlankPathsAndUnsupportedSchemes() {
        val settings = EndpointSettings("https://api.example.test", FakeEndpointStore())

        assertFailsWith<InvalidApiBaseUrl> { settings.save("  ") }
        assertFailsWith<InvalidApiBaseUrl> { settings.save("https://api.example.test/v1") }
        assertFailsWith<InvalidApiBaseUrl> { settings.save("ftp://api.example.test") }
    }

    @Test
    fun resetClearsTheOverrideAndRestoresTheBuildDefault() {
        val store = FakeEndpointStore("https://staging.example.test")
        val settings = EndpointSettings("https://api.example.test", store)

        settings.reset()

        assertEquals("https://api.example.test", settings.effectiveBaseUrl)
        assertEquals(null, store.value)
    }
}

private class FakeEndpointStore(initial: String? = null) : EndpointOverrideStore {
    var value: String? = initial
    override fun read(): String? = value
    override fun write(value: String?) { this.value = value }
}
