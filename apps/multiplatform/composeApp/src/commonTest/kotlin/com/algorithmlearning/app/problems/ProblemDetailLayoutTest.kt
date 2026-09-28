package com.algorithmlearning.app.problems

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class ProblemDetailLayoutTest {
    @Test
    fun selectsSingleColumnBelowTheLearningDetailBreakpoint() {
        assertEquals(ProblemDetailLayout.SINGLE_COLUMN, problemDetailLayoutFor(839.dp))
    }

    @Test
    fun selectsDescriptionAndLearningColumnsAtTheLearningDetailBreakpoint() {
        assertEquals(ProblemDetailLayout.TWO_COLUMN, problemDetailLayoutFor(840.dp))
    }
}
