package com.algorithmlearning.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorithmlearning.app.auth.AuthScreen
import com.algorithmlearning.app.auth.AuthViewModel
import com.algorithmlearning.app.dashboard.DashboardActions
import com.algorithmlearning.app.dashboard.DashboardScreen
import com.algorithmlearning.app.dashboard.DashboardViewModel
import com.algorithmlearning.app.problems.ProblemsActions
import com.algorithmlearning.app.problems.ProblemsScreen
import com.algorithmlearning.app.problems.ProblemsViewModel
import com.algorithmlearning.app.review.ReviewActions
import com.algorithmlearning.app.review.ReviewScreen
import com.algorithmlearning.app.review.ReviewViewModel
import com.algorithmlearning.shared.AppContainer
import com.algorithmlearning.shared.AppDestination
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.AppStrings
import com.algorithmlearning.shared.StringCatalog

@Composable
fun App() {
    val container = remember { AppContainer() }
    var language by remember { mutableStateOf(container.currentLanguage()) }
    val strings = remember(language) { StringCatalog.of(language) }
    val scope = rememberCoroutineScope()
    val authViewModel = remember(container) { AuthViewModel(container.authSession, scope) }
    val authState by container.authSession.state.collectAsState()
    var restored by remember { mutableStateOf(false) }

    LaunchedEffect(container) {
        container.authSession.restore()
        restored = true
    }

    AlgorithmLearningTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val session = authState.session
            when {
                session != null -> SignedInApp(
                    container = container,
                    strings = strings,
                    email = session.user.email,
                    onToggleLanguage = { language = toggleLanguage(language) },
                    onSignOut = authViewModel::logout,
                )
                !restored -> RestoringSession(strings)
                else -> {
                    val form by authViewModel.form.collectAsState()
                    AuthScreen(
                        state = form,
                        strings = strings,
                        onEmailChange = authViewModel::emailChanged,
                        onPasswordChange = authViewModel::passwordChanged,
                        onSubmit = authViewModel::submit,
                        onToggleMode = authViewModel::toggleMode,
                    )
                }
            }
        }
    }
}

private fun toggleLanguage(language: AppLanguage): AppLanguage = when (language) {
    AppLanguage.ENGLISH -> AppLanguage.TRADITIONAL_CHINESE
    AppLanguage.TRADITIONAL_CHINESE -> AppLanguage.ENGLISH
}

@Composable
private fun RestoringSession(strings: AppStrings) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(strings.authRestoringMessage)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignedInApp(
    container: AppContainer,
    strings: AppStrings,
    email: String,
    onToggleLanguage: () -> Unit,
    onSignOut: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val problemsViewModel = remember(container) {
        ProblemsViewModel(
            problems = container.problemRepository,
            tags = container.tagRepository,
            solutions = container.solutionRepository,
            scope = scope,
        )
    }
    LaunchedEffect(problemsViewModel) { problemsViewModel.initialize() }

    val reviewViewModel = remember(container) {
        ReviewViewModel(
            reviews = container.reviewRepository,
            problems = container.problemRepository,
            scope = scope,
        )
    }

    val dashboardViewModel = remember(container) {
        DashboardViewModel(dashboard = container.dashboardRepository, scope = scope)
    }

    val problemsState by problemsViewModel.state.collectAsState()
    val reviewState by reviewViewModel.state.collectAsState()
    val dashboardState by dashboardViewModel.state.collectAsState()
    val backStack by container.navigator.backStack.collectAsState()
    val current = backStack.last()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(current) {
        when (current) {
            AppDestination.Dashboard -> dashboardViewModel.load()
            AppDestination.Review -> reviewViewModel.initialize()
            else -> Unit
        }
    }

    LaunchedEffect(problemsState.message) {
        problemsState.message?.let { message ->
            snackbarHostState.showSnackbar(message.label(strings))
            problemsViewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = strings.appName) })
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (current) {
                AppDestination.Dashboard -> DashboardScreen(
                    state = dashboardState,
                    strings = strings,
                    actions = DashboardActions(
                        refresh = dashboardViewModel::refresh,
                        addProblem = {
                            problemsViewModel.newProblem()
                            container.navigator.resetTo(AppDestination.Problems)
                        },
                    ),
                )
                AppDestination.Problems -> ProblemsScreen(
                    state = problemsState,
                    strings = strings,
                    actions = problemsActions(
                        viewModel = problemsViewModel,
                        onReviewProblem = { id ->
                            reviewViewModel.openProblem(id)
                            container.navigator.resetTo(AppDestination.Review)
                        },
                    ),
                )
                AppDestination.Review -> BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    ReviewScreen(
                        state = reviewState,
                        strings = strings,
                        actions = reviewActions(reviewViewModel),
                        twoPane = maxWidth >= 840.dp,
                    )
                }
                AppDestination.Settings -> SettingsContent(
                    strings = strings,
                    email = email,
                    onToggleLanguage = onToggleLanguage,
                    onSignOut = onSignOut,
                )
            }
        }
    }
}

private fun problemsActions(
    viewModel: ProblemsViewModel,
    onReviewProblem: (String) -> Unit,
): ProblemsActions = ProblemsActions(
    refresh = viewModel::refresh,
    searchChanged = viewModel::searchChanged,
    search = viewModel::search,
    difficultyFilterChanged = viewModel::difficultyFilterChanged,
    platformFilterChanged = viewModel::platformFilterChanged,
    reviewStatusFilterChanged = viewModel::reviewStatusFilterChanged,
    toggleTag = viewModel::toggleTagFilter,
    previousPage = viewModel::previousPage,
    nextPage = viewModel::nextPage,
    openProblem = viewModel::openProblem,
    newProblem = viewModel::newProblem,
    editProblem = viewModel::editProblem,
    cancelEditor = viewModel::cancelEditor,
    editorTitleChanged = viewModel::editorTitleChanged,
    editorPlatformChanged = viewModel::editorPlatformChanged,
    editorDifficultyChanged = viewModel::editorDifficultyChanged,
    editorNotesChanged = viewModel::editorNotesChanged,
    editorTagNameChanged = viewModel::editorTagNameChanged,
    toggleEditorTag = viewModel::toggleEditorTag,
    saveProblem = viewModel::saveProblem,
    createTag = viewModel::createTag,
    requestDeleteProblem = viewModel::requestDeleteProblem,
    cancelDeleteProblem = viewModel::cancelDeleteProblem,
    confirmDeleteProblem = viewModel::confirmDeleteProblem,
    backToList = viewModel::backToList,
    solutionLanguageChanged = viewModel::solutionLanguageChanged,
    solutionCodeChanged = viewModel::solutionCodeChanged,
    solutionExplanationChanged = viewModel::solutionExplanationChanged,
    saveSolution = viewModel::saveSolution,
    deleteSolution = viewModel::deleteSolution,
    reviewProblem = onReviewProblem,
)

private fun reviewActions(viewModel: ReviewViewModel): ReviewActions = ReviewActions(
    refreshDue = viewModel::refreshDue,
    openProblem = viewModel::openProblem,
    exitReview = viewModel::exitReview,
    startThinking = viewModel::startThinking,
    revealHint = viewModel::revealHint,
    revealApproach = viewModel::revealApproach,
    revealSolution = viewModel::revealSolution,
    rateConfidence = viewModel::rateConfidence,
    selectSolution = viewModel::selectSolution,
    confidenceSelected = viewModel::confidenceSelected,
    notesChanged = viewModel::notesChanged,
    submitReview = viewModel::submitReview,
)

@Composable
private fun SettingsContent(
    strings: AppStrings,
    email: String,
    onToggleLanguage: () -> Unit,
    onSignOut: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = strings.settingsTitle,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "${strings.authSignedInAsLabel}: $email",
            modifier = Modifier.padding(top = 12.dp),
        )
        Button(
            modifier = Modifier.padding(top = 12.dp),
            onClick = onToggleLanguage,
        ) {
            Text(text = strings.switchLanguageAction)
        }
        Button(
            modifier = Modifier.padding(top = 12.dp),
            onClick = onSignOut,
        ) {
            Text(text = strings.authSignOutAction)
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
