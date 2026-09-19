package com.algorithmlearning.shared

/**
 * Manual dependency-injection composition root. Screens receive everything they
 * need through [AppContainer]; no composable reaches a repository or a service
 * locator directly.
 */
class AppContainer {
    val navigator: Navigator = Navigator()

    private var language: AppLanguage = AppLanguage.ENGLISH

    fun currentLanguage(): AppLanguage = language

    fun switchLanguage(next: AppLanguage): AppLanguage {
        language = next
        return language
    }

    fun strings(language: AppLanguage = this.language): AppStrings = StringCatalog.of(language)
}
