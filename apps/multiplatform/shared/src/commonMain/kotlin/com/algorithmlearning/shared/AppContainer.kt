package com.algorithmlearning.shared

import com.algorithmlearning.shared.auth.AuthRemote
import com.algorithmlearning.shared.auth.AuthRepository
import com.algorithmlearning.shared.auth.AuthSessionHolder
import com.algorithmlearning.shared.auth.RemoteAuthRepository
import com.algorithmlearning.shared.auth.data.DEFAULT_API_BASE_URL
import com.algorithmlearning.shared.auth.data.KtorAuthRemote
import com.algorithmlearning.shared.auth.data.createAuthHttpClient
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

    private var language: AppLanguage = AppLanguage.ENGLISH

    fun currentLanguage(): AppLanguage = language

    fun switchLanguage(next: AppLanguage): AppLanguage {
        language = next
        return language
    }

    fun strings(language: AppLanguage = this.language): AppStrings = StringCatalog.of(language)
}
