package com.algorithmlearning.shared

/**
 * Immutable, language-resolved text. The UI never concatenates raw strings, so
 * every screen renders through this catalog.
 */
data class AppStrings(
    val appName: String,
    val dashboardTitle: String,
    val problemsTitle: String,
    val reviewTitle: String,
    val settingsTitle: String,
    val languageLabel: String,
    val switchLanguageAction: String,
)

object StringCatalog {
    fun of(language: AppLanguage): AppStrings = when (language) {
        AppLanguage.ENGLISH -> AppStrings(
            appName = "Algorithm Learning",
            dashboardTitle = "Dashboard",
            problemsTitle = "Problems",
            reviewTitle = "Review",
            settingsTitle = "Settings",
            languageLabel = "Language",
            switchLanguageAction = "Switch language",
        )
        AppLanguage.TRADITIONAL_CHINESE -> AppStrings(
            appName = "演算法學習",
            dashboardTitle = "儀表板",
            problemsTitle = "題目",
            reviewTitle = "複習",
            settingsTitle = "設定",
            languageLabel = "語言",
            switchLanguageAction = "切換語言",
        )
    }
}
