package dev.algorithmlearning.api.reviews.application;

import static org.assertj.core.api.Assertions.assertThat;

import dev.algorithmlearning.api.problems.application.Problem;
import dev.algorithmlearning.api.problems.application.ProblemRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReviewServiceReviewTest {

    private static final UUID USER = UUID.fromString("00000000-0000-0000-0000-0000000000aa");
    private static final UUID PROBLEM = UUID.fromString("00000000-0000-0000-0000-0000000000bb");
    private static final Instant AT = Instant.parse("2026-09-20T00:00:00Z");

    @Test
    void createPersistsAnAdaptiveSnapshotForANewProblem() {
        var problems = new FakeProblemRepository();
        problems.add(problem(PROBLEM, AT));
        var reviews = new FakeReviewRepository();
        var service = new ReviewService(problems, reviews, Clock.fixed(AT, ZoneOffset.UTC));

        var created = service.create(USER, PROBLEM, 3, "  use a hash map  ");

        assertThat(created).isPresent();
        var review = created.orElseThrow();
        assertThat(review.policyVersion()).isEqualTo("adaptive-v1");
        assertThat(review.intervalDays()).isEqualTo(4);
        assertThat(review.easeFactor()).isEqualByComparingTo("2.50");
        assertThat(review.repetitions()).isEqualTo(1);
        assertThat(review.previousIntervalDays()).isNull();
        assertThat(review.previousEaseFactor()).isNull();
        assertThat(review.notes()).isEqualTo("use a hash map");
        assertThat(review.nextReviewAt()).isEqualTo(AT.plus(Duration.ofDays(4)));
        assertThat(reviews.saved).hasSize(1);
    }

    @Test
    void createUsesThePreviousAdaptiveIntervalOnARepeatReview() {
        var problems = new FakeProblemRepository();
        problems.add(problem(PROBLEM, AT));
        var reviews = new FakeReviewRepository();
        var first = new ReviewService(problems, reviews, Clock.fixed(AT, ZoneOffset.UTC));
        first.create(USER, PROBLEM, 3, null);
        var later = AT.plus(Duration.ofDays(4));
        var second = new ReviewService(problems, reviews, Clock.fixed(later, ZoneOffset.UTC));

        var review = second.create(USER, PROBLEM, 4, null).orElseThrow();

        assertThat(review.intervalDays()).isEqualTo(11);
        assertThat(review.previousIntervalDays()).isEqualTo(4);
        assertThat(review.previousEaseFactor()).isEqualByComparingTo("2.50");
        assertThat(review.easeFactor()).isEqualByComparingTo("2.65");
    }

    @Test
    void createIsEmptyForAnotherOwnersProblem() {
        var service = new ReviewService(new FakeProblemRepository(), new FakeReviewRepository(), Clock.fixed(AT, ZoneOffset.UTC));
        assertThat(service.create(USER, PROBLEM, 3, null)).isEmpty();
    }

    @Test
    void summariesDeriveStatusAndMetadataFromTheLatestEvent() {
        var problems = new FakeProblemRepository();
        problems.add(problem(PROBLEM, AT));
        var reviews = new FakeReviewRepository();
        new ReviewService(problems, reviews, Clock.fixed(AT, ZoneOffset.UTC)).create(USER, PROBLEM, 3, null);

        var scheduled = new ReviewService(problems, reviews, Clock.fixed(AT.plus(Duration.ofDays(1)), ZoneOffset.UTC));
        var scheduledSummary = scheduled.summaries(USER, List.of(PROBLEM)).get(PROBLEM);
        assertThat(scheduledSummary.status()).isEqualTo("scheduled");
        assertThat(scheduledSummary.confidence()).isEqualTo(3);
        assertThat(scheduledSummary.reviewCount()).isEqualTo(1);
        assertThat(scheduledSummary.intervalDays()).isEqualTo(4);
        assertThat(scheduledSummary.scheduleExplanationKey()).isEqualTo("schedule.adaptive.rated");

        var due = new ReviewService(problems, reviews, Clock.fixed(AT.plus(Duration.ofDays(5)), ZoneOffset.UTC));
        assertThat(due.summaries(USER, List.of(PROBLEM)).get(PROBLEM).status()).isEqualTo("due");
    }

    @Test
    void summariesReportANeverReviewedProblem() {
        var service = new ReviewService(new FakeProblemRepository(), new FakeReviewRepository(), Clock.fixed(AT, ZoneOffset.UTC));
        var summary = service.summaries(USER, List.of(PROBLEM)).get(PROBLEM);
        assertThat(summary.status()).isEqualTo("neverReviewed");
        assertThat(summary.confidence()).isNull();
        assertThat(summary.reviewCount()).isZero();
        assertThat(summary.intervalDays()).isNull();
        assertThat(summary.scheduleExplanationKey()).isEqualTo("schedule.neverReviewed");
    }

    private static Problem problem(UUID id, Instant createdAt) {
        return new Problem(id, USER, "Two Sum", "leetcode", null, null, "easy",
                null, null, null, null, null, null, null, createdAt, createdAt, List.of());
    }

    private static final class FakeProblemRepository implements ProblemRepository {
        private final Map<UUID, Problem> problems = new HashMap<>();

        void add(Problem problem) {
            problems.put(problem.id(), problem);
        }

        public Problem save(Problem problem) {
            problems.put(problem.id(), problem);
            return problem;
        }

        public Optional<Problem> findByIdAndUserId(UUID id, UUID userId) {
            var problem = problems.get(id);
            return problem != null && problem.userId().equals(userId) ? Optional.of(problem) : Optional.empty();
        }

        public List<Problem> findAllByUserId(UUID userId) {
            return problems.values().stream().filter(problem -> problem.userId().equals(userId)).toList();
        }

        public void deleteByIdAndUserId(UUID id, UUID userId) {
            problems.remove(id);
        }
    }

    private static final class FakeReviewRepository implements ReviewRepository {
        private final List<Review> saved = new ArrayList<>();
        private final Map<UUID, List<Review>> byProblem = new HashMap<>();

        public Review save(Review review) {
            saved.add(review);
            byProblem.computeIfAbsent(review.problemId(), key -> new ArrayList<>()).add(review);
            return review;
        }

        public Optional<Review> latest(UUID userId, UUID problemId) {
            var list = byProblem.get(problemId);
            return list == null || list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() - 1));
        }

        public List<UUID> dueProblemIds(UUID userId, Instant now, int limit, int offset) {
            return List.of();
        }

        public Map<UUID, ProblemReviewState> reviewStates(UUID userId, Collection<UUID> problemIds) {
            var states = new HashMap<UUID, ProblemReviewState>();
            for (var problemId : problemIds) {
                var list = byProblem.get(problemId);
                if (list != null && !list.isEmpty()) states.put(problemId, new ProblemReviewState(list.get(list.size() - 1), list.size()));
            }
            return states;
        }

        public List<Review> history(UUID userId, UUID problemId, int limit, int offset) {
            return List.of();
        }
    }
}
