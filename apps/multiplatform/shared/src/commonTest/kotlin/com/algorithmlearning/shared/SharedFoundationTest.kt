package com.algorithmlearning.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SharedFoundationTest {

    @Test
    fun navigatorTracksBackStack() {
        val navigator = Navigator()
        assertEquals(AppDestination.Dashboard, navigator.current)
        assertFalse(navigator.canGoBack)

        navigator.navigateTo(AppDestination.Problems)
        assertEquals(AppDestination.Problems, navigator.current)
        assertTrue(navigator.canGoBack)
        assertEquals(2, navigator.depth)

        assertTrue(navigator.goBack())
        assertEquals(AppDestination.Dashboard, navigator.current)
    }

    @Test
    fun navigatingToTheCurrentDestinationIsANoop() {
        val navigator = Navigator()
        navigator.navigateTo(AppDestination.Dashboard)
        assertEquals(1, navigator.depth)
    }

    @Test
    fun backStackCannotUnderflow() {
        val navigator = Navigator()
        assertFalse(navigator.goBack())
        assertEquals(1, navigator.depth)
    }

    @Test
    fun resetReplacesHistory() {
        val navigator = Navigator()
        navigator.navigateTo(AppDestination.Problems)
        navigator.navigateTo(AppDestination.Review)
        navigator.resetTo(AppDestination.Settings)
        assertEquals(AppDestination.Settings, navigator.current)
        assertEquals(1, navigator.depth)
    }

    @Test
    fun localizationResolvesBothLanguages() {
        assertEquals("Dashboard", StringCatalog.of(AppLanguage.ENGLISH).dashboardTitle)
        assertEquals("儀表板", StringCatalog.of(AppLanguage.TRADITIONAL_CHINESE).dashboardTitle)
        assertNotEquals(
            StringCatalog.of(AppLanguage.ENGLISH),
            StringCatalog.of(AppLanguage.TRADITIONAL_CHINESE),
        )
    }

    @Test
    fun containerIsTheCompositionRoot() {
        val container = AppContainer()
        assertEquals(AppLanguage.ENGLISH, container.currentLanguage())
        assertEquals("Problems", container.strings().problemsTitle)

        container.switchLanguage(AppLanguage.TRADITIONAL_CHINESE)
        assertEquals(AppLanguage.TRADITIONAL_CHINESE, container.currentLanguage())
        assertEquals("題目", container.strings().problemsTitle)
    }

    @Test
    fun domainValuesAreImmutable() {
        val first = Problem(id = "1", title = "Two Sum", difficulty = Difficulty.EASY)
        val copy = first.copy()
        assertEquals(first, copy)
        assertNotEquals(first, copy.copy(difficulty = Difficulty.HARD))
    }
}
