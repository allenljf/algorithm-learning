package dev.algorithmlearning.api.reviews.application;

import java.time.Instant;

/**
 * The derived, immutable review summary for a problem. {@code status} is
 * {@code neverReviewed}, {@code due}, or {@code scheduled} from the latest
 * event and the injected clock. Schedule metadata is null for
 * {@code neverReviewed} and historical {@code fixed-v1} rows.
 */
public record ReviewSummaryData(String status, Integer confidence, Instant lastReviewedAt, Instant nextReviewAt,
                                int reviewCount, Integer intervalDays, String scheduleExplanationKey) { }
