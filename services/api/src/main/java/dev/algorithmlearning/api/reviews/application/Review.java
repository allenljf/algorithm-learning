package dev.algorithmlearning.api.reviews.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * An immutable review event. The adaptive snapshot columns are null for
 * historical {@code fixed-v1} events and carry the persisted scheduling inputs
 * and outputs for {@code adaptive-v1} events.
 */
public record Review(UUID id, UUID problemId, int confidence, Instant reviewedAt, Instant nextReviewAt,
                     String notes, String policyVersion,
                     Integer previousIntervalDays, BigDecimal previousEaseFactor,
                     Integer intervalDays, BigDecimal easeFactor, Integer repetitions) { }
