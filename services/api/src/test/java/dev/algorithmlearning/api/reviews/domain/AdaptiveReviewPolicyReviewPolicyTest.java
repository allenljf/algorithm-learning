package dev.algorithmlearning.api.reviews.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AdaptiveReviewPolicyReviewPolicyTest {

    private final AdaptiveReviewPolicy policy = new AdaptiveReviewPolicy();
    private final Instant at = Instant.parse("2026-09-20T00:00:00Z");

    @Test
    void bootstrapsAFixedHistoryProblemForEveryConfidence() {
        var c0 = policy.next(at, 0, null, null, null);
        assertThat(c0.intervalDays()).isEqualTo(1);
        assertThat(c0.repetitions()).isZero();
        assertThat(c0.easeFactor()).isEqualByComparingTo("2.30");
        assertThat(c0.previousIntervalDays()).isNull();
        assertThat(c0.previousEaseFactor()).isNull();
        assertThat(c0.nextReviewAt()).isEqualTo(at.plus(Duration.ofDays(1)));

        assertThat(policy.next(at, 1, null, null, null).easeFactor()).isEqualByComparingTo("2.35");

        var c2 = policy.next(at, 2, null, null, null);
        assertThat(c2.intervalDays()).isEqualTo(2);
        assertThat(c2.repetitions()).isEqualTo(1);
        assertThat(c2.easeFactor()).isEqualByComparingTo("2.45");

        var c3 = policy.next(at, 3, null, null, null);
        assertThat(c3.intervalDays()).isEqualTo(4);
        assertThat(c3.repetitions()).isEqualTo(1);
        assertThat(c3.easeFactor()).isEqualByComparingTo("2.50");

        var c4 = policy.next(at, 4, null, null, null);
        assertThat(c4.intervalDays()).isEqualTo(7);
        assertThat(c4.repetitions()).isEqualTo(1);
        assertThat(c4.easeFactor()).isEqualByComparingTo("2.65");
    }

    @Test
    void growsAndShrinksFromThePreviousSchedule() {
        var c3 = policy.next(at, 3, 4, new BigDecimal("2.50"), 1);
        assertThat(c3.intervalDays()).isEqualTo(10);
        assertThat(c3.repetitions()).isEqualTo(2);
        assertThat(c3.previousIntervalDays()).isEqualTo(4);
        assertThat(c3.previousEaseFactor()).isEqualByComparingTo("2.50");
        assertThat(c3.nextReviewAt()).isEqualTo(at.plus(Duration.ofDays(10)));

        var c4 = policy.next(at, 4, 4, new BigDecimal("2.50"), 1);
        assertThat(c4.intervalDays()).isEqualTo(11);
        assertThat(c4.easeFactor()).isEqualByComparingTo("2.65");

        var c2 = policy.next(at, 2, 4, new BigDecimal("2.50"), 3);
        assertThat(c2.intervalDays()).isEqualTo(5);
        assertThat(c2.repetitions()).isEqualTo(3);
        assertThat(c2.easeFactor()).isEqualByComparingTo("2.45");

        assertThat(policy.next(at, 0, 4, new BigDecimal("2.50"), 3).easeFactor()).isEqualByComparingTo("2.30");
        assertThat(policy.next(at, 1, 4, new BigDecimal("2.50"), 3).easeFactor()).isEqualByComparingTo("2.35");
    }

    @Test
    void clampsTheEaseFactorToItsBounds() {
        assertThat(policy.next(at, 0, 4, new BigDecimal("1.30"), 1).easeFactor()).isEqualByComparingTo("1.30");
        assertThat(policy.next(at, 4, 4, new BigDecimal("3.00"), 1).easeFactor()).isEqualByComparingTo("3.00");
    }

    @Test
    void roundsIntervalsHalfUp() {
        assertThat(policy.next(at, 3, 5, new BigDecimal("2.10"), 1).intervalDays()).isEqualTo(11);
    }

    @Test
    void rejectsAnOutOfRangeConfidence() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> policy.next(at, 5, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
