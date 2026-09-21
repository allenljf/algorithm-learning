package com.algorithmlearning.app

import androidx.compose.ui.window.ComposeUIViewController
import com.algorithmlearning.shared.EndpointOverrideStore
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIViewController

private const val endpointPreferenceKey = "api-base-url"

/**
 * The iOS shell calls this entry point with its non-secret build default. The
 * shared [App] still owns endpoint validation, selection, and container resets.
 */
fun MainViewController(defaultApiBaseUrl: String): UIViewController = ComposeUIViewController {
    App(
        defaultApiBaseUrl = defaultApiBaseUrl,
        endpointStore = IosEndpointOverrideStore,
    )
}

private object IosEndpointOverrideStore : EndpointOverrideStore {
    private val defaults: NSUserDefaults
        get() = NSUserDefaults.standardUserDefaults

    override fun read(): String? = defaults.stringForKey(endpointPreferenceKey)

    override fun write(value: String?) {
        if (value == null) defaults.removeObjectForKey(endpointPreferenceKey)
        else defaults.setObject(value, endpointPreferenceKey)
    }
}
