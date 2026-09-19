package com.algorithmlearning.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorithmlearning.shared.AppContainer
import com.algorithmlearning.shared.AppDestination
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.AppStrings
import com.algorithmlearning.shared.StringCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val container = remember { AppContainer() }
    var language by remember { mutableStateOf(container.currentLanguage()) }
    val strings = remember(language) { StringCatalog.of(language) }
    val backStack by container.navigator.backStack.collectAsState()
    val current = backStack.last()

    AlgorithmLearningTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = strings.appName) },
                    )
                },
                bottomBar = {
                    NavigationBar {
                        TopLevelDestination.entries.forEach { destination ->
                            NavigationBarItem(
                                selected = current == destination.destination,
                                onClick = { container.navigator.resetTo(destination.destination) },
                                icon = {},
                                label = { Text(destination.label(strings)) },
                            )
                        }
                    }
                },
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    DestinationContent(
                        destination = current,
                        strings = strings,
                        onToggleLanguage = {
                            language = if (language == AppLanguage.ENGLISH) {
                                AppLanguage.TRADITIONAL_CHINESE
                            } else {
                                AppLanguage.ENGLISH
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinationContent(
    destination: AppDestination,
    strings: AppStrings,
    onToggleLanguage: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val title = when (destination) {
            AppDestination.Dashboard -> strings.dashboardTitle
            AppDestination.Problems -> strings.problemsTitle
            AppDestination.Review -> strings.reviewTitle
            AppDestination.Settings -> strings.settingsTitle
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        if (destination == AppDestination.Settings) {
            Text(
                text = "${strings.languageLabel}: ${strings.appName}",
                modifier = Modifier.padding(top = 12.dp),
            )
            Button(
                modifier = Modifier.padding(top = 12.dp),
                onClick = onToggleLanguage,
            ) {
                Text(text = strings.switchLanguageAction)
            }
        }
    }
}

private enum class TopLevelDestination(val destination: AppDestination) {
    Dashboard(AppDestination.Dashboard),
    Problems(AppDestination.Problems),
    Review(AppDestination.Review),
    Settings(AppDestination.Settings);

    fun label(strings: AppStrings): String = when (this) {
        Dashboard -> strings.dashboardTitle
        Problems -> strings.problemsTitle
        Review -> strings.reviewTitle
        Settings -> strings.settingsTitle
    }
}
