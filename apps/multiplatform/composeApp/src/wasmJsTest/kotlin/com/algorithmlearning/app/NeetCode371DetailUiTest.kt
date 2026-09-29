package com.algorithmlearning.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.algorithmlearning.app.problems.ProblemDetailState
import com.algorithmlearning.app.problems.ProblemsScreen
import com.algorithmlearning.app.problems.ProblemsUiState
import com.algorithmlearning.app.problems.ProblemsView
import com.algorithmlearning.shared.AppLanguage
import com.algorithmlearning.shared.StringCatalog
import com.algorithmlearning.shared.library.Tag
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NeetCode371DetailUiTest {
    @Test
    fun detailRendersTheOfficialBilingual371LessonInBothColumns() = runComposeUiTest {
        var openedUrl: String? = null
        val detail = problemDetail(
            "371",
            "371. Sum of Two Integers",
            notes = "中文題意說明：不能使用加減法\n\n[Chinese Summary]\n中文摘要：xor 與 carry",
            tags = listOf(Tag("bit", "Bit Manipulation")),
        ).copy(
            externalUrl = "https://leetcode.com/problems/sum-of-two-integers/",
            description = "Given two integers a and b, return their sum.\n\n[Official Examples]\nInput: a = 1, b = 2\nOutput: 3",
            keyInsight = "xor 是不含進位的和",
            timeComplexity = "時間 O(1)",
            spaceComplexity = "空間 O(1)",
            interviewNotes = "assertEquals(3, Solution().getSum(1, 2))",
        )
        val solution = problemSolution(
            id = "solution-371",
            problemId = "371",
            code = "// 中文註解\nclass Solution",
            explanation = "中文解題思路內容：以 xor 計算",
        )
        setContent {
            CompositionLocalProvider(LocalUriHandler provides object : UriHandler {
                override fun openUri(uri: String) {
                    openedUrl = uri
                }
            }) {
                ProblemsScreen(
                    state = ProblemsUiState(
                        view = ProblemsView.DETAIL,
                        detail = ProblemDetailState(detail = detail, solutions = listOf(solution)),
                    ),
                    strings = StringCatalog.of(AppLanguage.TRADITIONAL_CHINESE),
                )
            }
        }
        onNodeWithText("https://leetcode.com/problems/sum-of-two-integers/")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertEquals("https://leetcode.com/problems/sum-of-two-integers/", openedUrl)
        onNodeWithText("Given two integers a and b, return their sum.").assertIsDisplayed()
        onNodeWithText("Input: a = 1, b = 2\nOutput: 3").assertIsDisplayed()
        onNodeWithText("中文題意說明：不能使用加減法").assertIsDisplayed()
        onNodeWithText("中文摘要：xor 與 carry").assertIsDisplayed()
        onNodeWithText("中文解題思路內容：以 xor 計算").assertIsDisplayed()
        onNodeWithText("Bit Manipulation").assertIsDisplayed()
        onNodeWithText("時間 O(1)").assertIsDisplayed()
        onNodeWithText("空間 O(1)").assertIsDisplayed()
        onNodeWithText("// 中文註解\nclass Solution").assertIsDisplayed()
        onNodeWithText("assertEquals(3, Solution().getSum(1, 2))").assertIsDisplayed()
    }
}
