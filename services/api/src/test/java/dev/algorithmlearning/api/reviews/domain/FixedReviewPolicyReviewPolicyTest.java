package dev.algorithmlearning.api.reviews.domain;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.*; import org.junit.jupiter.api.Test;
class FixedReviewPolicyReviewPolicyTest { @Test void schedulesConfidenceFourFourteenDaysLater(){var at=Instant.parse("2026-09-02T00:00:00Z");assertThat(new FixedReviewPolicy().nextReviewAt(at,4)).isEqualTo(at.plus(Duration.ofDays(14)));} }
