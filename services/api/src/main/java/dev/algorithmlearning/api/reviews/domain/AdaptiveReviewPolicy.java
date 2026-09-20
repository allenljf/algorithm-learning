package dev.algorithmlearning.api.reviews.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

/**
 * The deterministic adaptive-v1 scheduling policy. It derives the next interval
 * from the previous persisted schedule and the submitted confidence, keeps the
 * ease factor in {@code [1.30, 3.00]}, and is reproducible from its inputs.
 *
 * <p>A problem whose latest event is {@code fixed-v1} (or that has never been
 * reviewed) bootstraps with {@code repetitions = 0}, {@code intervalDays = 0},
 * and {@code easeFactor = 2.50}; the prior event is used only to know that the
 * problem has been reviewed.
 */
public final class AdaptiveReviewPolicy {

    public static final String VERSION = "adaptive-v1";

    private static final BigDecimal INITIAL_EASE = new BigDecimal("2.50");
    private static final BigDecimal MIN_EASE = new BigDecimal("1.30");
    private static final BigDecimal MAX_EASE = new BigDecimal("3.00");
    private static final BigDecimal GROWTH = new BigDecimal("1.20");
    private static final BigDecimal STEP_DOWN_LARGE = new BigDecimal("-0.20");
    private static final BigDecimal STEP_DOWN_MEDIUM = new BigDecimal("-0.15");
    private static final BigDecimal STEP_DOWN_SMALL = new BigDecimal("-0.05");
    private static final BigDecimal STEP_UP = new BigDecimal("0.15");

    public Result next(Instant reviewedAt, int confidence,
                       Integer previousIntervalDays, BigDecimal previousEaseFactor, Integer previousRepetitions) {
        if (confidence < 0 || confidence > 4) throw new IllegalArgumentException("Invalid confidence.");
        boolean bootstrap = previousIntervalDays == null;
        int priorRepetitions = previousRepetitions == null ? 0 : previousRepetitions;
        int priorInterval = previousIntervalDays == null ? 0 : previousIntervalDays;
        BigDecimal priorEase = previousEaseFactor == null ? INITIAL_EASE : previousEaseFactor;

        int repetitions;
        int intervalDays;
        BigDecimal delta;
        switch (confidence) {
            case 0 -> {
                repetitions = 0;
                intervalDays = 1;
                delta = STEP_DOWN_LARGE;
            }
            case 1 -> {
                repetitions = 0;
                intervalDays = 1;
                delta = STEP_DOWN_MEDIUM;
            }
            case 2 -> {
                repetitions = Math.max(priorRepetitions, 1);
                intervalDays = Math.max(2, roundHalfUp(new BigDecimal(priorInterval).multiply(GROWTH)));
                delta = STEP_DOWN_SMALL;
            }
            case 3 -> {
                repetitions = priorRepetitions + 1;
                intervalDays = bootstrap
                        ? 4
                        : Math.max(4, roundHalfUp(new BigDecimal(priorInterval).multiply(priorEase)));
                delta = BigDecimal.ZERO;
            }
            default -> {
                repetitions = priorRepetitions + 1;
                intervalDays = bootstrap
                        ? 7
                        : Math.max(7, roundHalfUp(new BigDecimal(priorInterval).multiply(priorEase.add(STEP_UP))));
                delta = STEP_UP;
            }
        }

        BigDecimal easeFactor = clamp(priorEase.add(delta));
        Instant nextReviewAt = reviewedAt.plus(Duration.ofDays(intervalDays));
        return new Result(
                intervalDays,
                easeFactor,
                repetitions,
                bootstrap ? null : priorInterval,
                bootstrap ? null : priorEase,
                nextReviewAt);
    }

    private static int roundHalfUp(BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    private static BigDecimal clamp(BigDecimal value) {
        return value.max(MIN_EASE).min(MAX_EASE).setScale(2, RoundingMode.HALF_UP);
    }

    public record Result(int intervalDays, BigDecimal easeFactor, int repetitions,
                         Integer previousIntervalDays, BigDecimal previousEaseFactor,
                         Instant nextReviewAt) { }
}
