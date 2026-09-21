package com.algorithmlearning.app

import android.os.Bundle
import android.content.pm.ApplicationInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.algorithmlearning.shared.EndpointOverrideStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App(
                defaultApiBaseUrl = if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) "http://10.0.2.2:8080" else "https://algorithm-learning-api-qvepavg7qa-de.run.app",
                endpointStore = SharedPreferencesEndpointStore(),
            )
        }
    }

    private fun SharedPreferencesEndpointStore(): EndpointOverrideStore = object : EndpointOverrideStore {
        private val preferences = getSharedPreferences("client-settings", MODE_PRIVATE)
        override fun read(): String? = preferences.getString("api-base-url", null)
        override fun write(value: String?) { preferences.edit().putString("api-base-url", value).apply() }
    }
}
