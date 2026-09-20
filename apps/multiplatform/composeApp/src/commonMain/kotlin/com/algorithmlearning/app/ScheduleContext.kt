package com.algorithmlearning.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.algorithmlearning.shared.AppStrings
import kotlin.time.Instant

/**
 * Renders the persisted, localized schedule context for a review summary or a
 * just-submitted review. It shows only values the server provided; a missing
 * key renders the never-reviewed explanation instead of inventing a date.
 */
@Composable
fun ScheduleContext(
    key: String?,
    confidence: Int?,
    intervalDays: Int?,
    nextReviewAt: Instant?,
    strings: AppStrings,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = 12.dp).testTag("schedule-context")) {
        Text(strings.reviewScheduleTitle, style = MaterialTheme.typography.titleSmall)
        Text(scheduleExplanation(key, confidence, intervalDays, strings))
        if (nextReviewAt != null && key != null) {
            Text(
                text = "${strings.reviewScheduleNextReviewLabel}: $nextReviewAt",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
