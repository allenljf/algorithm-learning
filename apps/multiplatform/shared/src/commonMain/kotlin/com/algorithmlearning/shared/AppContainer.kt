package com.algorithmlearning.shared

import com.algorithmlearning.shared.auth.AuthRemote
import com.algorithmlearning.shared.auth.AuthRepository
import com.algorithmlearning.shared.auth.AuthSessionHolder
import com.algorithmlearning.shared.auth.RemoteAuthRepository
import com.algorithmlearning.shared.auth.data.DEFAULT_API_BASE_URL
import com.algorithmlearning.shared.auth.data.KtorAuthRemote
import com.algorithmlearning.shared.auth.data.createAuthHttpClient
import com.algorithmlearning.shared.library.DashboardRepository
import com.algorithmlearning.shared.library.ProblemRepository
import com.algorithmlearning.shared.library.RemoteDashboardRepository
import com.algorithmlearning.shared.library.RemoteProblemRepository
import com.algorithmlearning.shared.library.RemoteReviewRepository
import com.algorithmlearning.shared.library.RemoteSolutionRepository
import com.algorithmlearning.shared.library.RemoteTagRepository
import com.algorithmlearning.shared.library.ReviewRepository
import com.algorithmlearning.shared.library.SolutionRepository
import com.algorithmlearning.shared.library.TagRepository
import com.algorithmlearning.shared.library.data.ApiClient
import com.algorithmlearning.shared.library.data.KtorDashboardRemote
import com.algorithmlearning.shared.library.data.KtorProblemRemote
import com.algorithmlearning.shared.library.data.KtorReviewRemote
import com.algorithmlearning.shared.library.data.KtorSolutionRemote
import com.algorithmlearning.shared.library.data.KtorTagRemote
import io.ktor.client.HttpClient
import kotlin.time.Clock

/**
 * Manual dependency-injection composition root. Screens receive everything they
 * need through [AppContainer]; no composable reaches a repository or a service
 * locator directly.
 *
 * Constructors take their seams as parameters so tests can substitute a fake
 * [HttpClient] or [Clock] without a service locator.
 */
class AppContainer(
    baseUrl: String = DEFAULT_API_BASE_URL,
    httpClient: HttpClient = createAuthHttpClient(),
    clock: Clock = Clock.System,
) {
    val navigator: Navigator = Navigator()

    val authRemote: AuthRemote = KtorAuthRemote(client = httpClient, baseUrl = baseUrl)

    val authRepository: AuthRepository = RemoteAuthRepository(remote = authRemote, clock = clock)

    val authSession: AuthSessionHolder = AuthSessionHolder(authRepository)

    private val apiClient: ApiClient = ApiClient(client = httpClient, baseUrl = baseUrl, tokens = authRepository)

    val problemRepository: ProblemRepository = RemoteProblemRepository(KtorProblemRemote(apiClient))

    val tagRepository: TagRepository = RemoteTagRepository(KtorTagRemote(apiClient))

    val solutionRepository: SolutionRepository = RemoteSolutionRepository(KtorSolutionRemote(apiClient))

    val reviewRepository: ReviewRepository = RemoteReviewRepository(KtorReviewRemote(apiClient))

    val dashboardRepository: DashboardRepository = RemoteDashboardRepository(KtorDashboardRemote(apiClient))

    private var language: AppLanguage = AppLanguage.ENGLISH

    fun currentLanguage(): AppLanguage = language

    fun switchLanguage(next: AppLanguage): AppLanguage {
        language = next
        return language
    }

    fun strings(language: AppLanguage = this.language): AppStrings = StringCatalog.of(language)
}
