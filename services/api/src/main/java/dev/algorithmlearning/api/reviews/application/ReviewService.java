package dev.algorithmlearning.api.reviews.application;

import dev.algorithmlearning.api.problems.application.ProblemRepository;
import dev.algorithmlearning.api.reviews.domain.AdaptiveReviewPolicy;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public class ReviewService {

    private final ProblemRepository problems;
    private final ReviewRepository reviews;
    private final Clock clock;
    private final AdaptiveReviewPolicy policy = new AdaptiveReviewPolicy();

    public ReviewService(ProblemRepository problems, ReviewRepository reviews, Clock clock) {
        this.problems = problems;
        this.reviews = reviews;
        this.clock = clock;
    }

    @Transactional
    public Optional<Review> create(UUID owner, UUID problemId, int confidence, String notes) {
        if (problems.findByIdAndUserId(problemId, owner).isEmpty()) return Optional.empty();
        var now = clock.instant();
        var previous = reviews.latest(owner, problemId).orElse(null);
        var result = policy.next(
                now,
                confidence,
                previous == null ? null : previous.intervalDays(),
                previous == null ? null : previous.easeFactor(),
                previous == null ? null : previous.repetitions());
        var review = new Review(
                UUID.randomUUID(),
                problemId,
                confidence,
                now,
                result.nextReviewAt(),
                blank(notes),
                AdaptiveReviewPolicy.VERSION,
                result.previousIntervalDays(),
                result.previousEaseFactor(),
                result.intervalDays(),
                result.easeFactor(),
                result.repetitions());
        return Optional.of(reviews.save(review));
    }

    public List<UUID> dueProblemIds(UUID owner, int page, int size) {
        return reviews.dueProblemIds(owner, clock.instant(), size, (page - 1) * size);
    }

    public Map<UUID, ReviewSummaryData> summaries(UUID owner, Collection<UUID> problemIds) {
        if (problemIds == null || problemIds.isEmpty()) return Map.of();
        var states = reviews.reviewStates(owner, problemIds);
        var now = clock.instant();
        var summaries = new LinkedHashMap<UUID, ReviewSummaryData>();
        for (var problemId : problemIds) summaries.put(problemId, summarize(states.get(problemId), now));
        return summaries;
    }

    public List<Review> history(UUID owner, UUID problemId, int page, int size) {
        return reviews.history(owner, problemId, size, (page - 1) * size);
    }

    private static ReviewSummaryData summarize(ProblemReviewState state, Instant now) {
        if (state == null) {
            return new ReviewSummaryData("neverReviewed", null, null, null, 0, null, "schedule.neverReviewed");
        }
        var latest = state.latest();
        var status = latest.nextReviewAt().isAfter(now) ? "scheduled" : "due";
        var key = AdaptiveReviewPolicy.VERSION.equals(latest.policyVersion())
                ? "schedule.adaptive.rated"
                : "schedule.fixed.rated";
        return new ReviewSummaryData(
                status,
                latest.confidence(),
                latest.reviewedAt(),
                latest.nextReviewAt(),
                state.reviewCount(),
                latest.intervalDays(),
                key);
    }

    private static String blank(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
