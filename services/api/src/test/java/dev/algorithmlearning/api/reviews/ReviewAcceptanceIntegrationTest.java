package dev.algorithmlearning.api.reviews;

import static org.assertj.core.api.Assertions.assertThat;

import dev.algorithmlearning.api.problems.application.Problem;
import dev.algorithmlearning.api.problems.application.ProblemRepository;
import dev.algorithmlearning.api.reviews.application.ReviewService;
import dev.algorithmlearning.api.reviews.data.JpaReviewRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Cross-system acceptance for adaptive review scheduling against real
 * PostgreSQL semantics: the V2 migration, adaptive-v1 persistence, fixed-v1
 * history compatibility, and latest-event due derivation.
 */
@Testcontainers(disabledWithoutDocker = true)
class ReviewAcceptanceIntegrationTest {

    private static final UUID USER = UUID.fromString("00000000-0000-0000-0000-0000000000aa");
    private static final UUID REVIEWED = UUID.fromString("00000000-0000-0000-0000-0000000000b1");
    private static final UUID NEVER = UUID.fromString("00000000-0000-0000-0000-0000000000b2");
    private static final UUID FIXED_HISTORY = UUID.fromString("00000000-0000-0000-0000-0000000000b3");
    private static final Instant AT = Instant.parse("2026-09-20T00:00:00Z");

    @Container
    static final PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void schedulesAdaptivelyAndDerivesDueFromTheLatestEvent() throws Exception {
        var source = new DriverManagerDataSource(postgresql.getJdbcUrl(), postgresql.getUsername(), postgresql.getPassword());
        Flyway.configure().dataSource(source).load().migrate();
        var jdbc = new NamedParameterJdbcTemplate(source);
        seed(jdbc);

        var reviews = new JpaReviewRepository(jdbc);
        var problems = new StubProblemRepository(REVIEWED, NEVER, FIXED_HISTORY);
        var service = new ReviewService(problems, reviews, Clock.fixed(AT, ZoneOffset.UTC));

        var first = service.create(USER, REVIEWED, 3, null).orElseThrow();
        assertThat(first.policyVersion()).isEqualTo("adaptive-v1");
        assertThat(first.intervalDays()).isEqualTo(4);
        assertThat(first.easeFactor()).isEqualByComparingTo("2.50");
        assertThat(first.repetitions()).isEqualTo(1);
        assertThat(first.previousIntervalDays()).isNull();
        assertThat(first.nextReviewAt()).isEqualTo(AT.plus(Duration.ofDays(4)));

        var summary = service.summaries(USER, List.of(REVIEWED)).get(REVIEWED);
        assertThat(summary.status()).isEqualTo("scheduled");
        assertThat(summary.intervalDays()).isEqualTo(4);
        assertThat(summary.scheduleExplanationKey()).isEqualTo("schedule.adaptive.rated");

        assertThat(reviews.dueProblemIds(USER, AT, 10, 0)).containsExactly(NEVER, FIXED_HISTORY);
        assertThat(reviews.dueProblemIds(USER, AT.plus(Duration.ofDays(5)), 10, 0))
                .containsExactly(NEVER, FIXED_HISTORY, REVIEWED);

        var repeat = new ReviewService(problems, reviews, Clock.fixed(AT.plus(Duration.ofDays(4)), ZoneOffset.UTC))
                .create(USER, REVIEWED, 4, null).orElseThrow();
        assertThat(repeat.intervalDays()).isEqualTo(11);
        assertThat(repeat.previousIntervalDays()).isEqualTo(4);
        assertThat(repeat.easeFactor()).isEqualByComparingTo("2.65");

        var bootstrap = new ReviewService(problems, reviews, Clock.fixed(AT, ZoneOffset.UTC))
                .create(USER, FIXED_HISTORY, 3, null).orElseThrow();
        assertThat(bootstrap.policyVersion()).isEqualTo("adaptive-v1");
        assertThat(bootstrap.intervalDays()).isEqualTo(4);
        assertThat(bootstrap.previousIntervalDays()).isNull();
    }

    private static void seed(NamedParameterJdbcTemplate jdbc) {
        jdbc.update("insert into users (id, email, password_hash) values (:id, 'owner@example.test', 'x')",
                new MapSqlParameterSource().addValue("id", USER));
        var at = java.sql.Timestamp.from(AT);
        for (var id : List.of(REVIEWED, NEVER, FIXED_HISTORY)) {
            jdbc.update("insert into problems (id, user_id, title, platform, difficulty, created_at) "
                    + "values (:id, :user, 'Two Sum', 'leetcode', 'easy', :at)",
                    new MapSqlParameterSource().addValue("id", id).addValue("user", USER).addValue("at", at));
        }
        jdbc.update("insert into reviews (id, problem_id, confidence, reviewed_at, next_review_at, policy_version) "
                + "values ('00000000-0000-0000-0000-0000000000c1', :problem, 4, :at, :at, 'fixed-v1')",
                new MapSqlParameterSource().addValue("problem", FIXED_HISTORY).addValue("at", at));
    }

    private static final class StubProblemRepository implements ProblemRepository {
        private final Map<UUID, Problem> problems = new HashMap<>();

        StubProblemRepository(UUID... ids) {
            for (var id : ids) {
                problems.put(id, new Problem(id, USER, "Two Sum", "leetcode", null, null, "easy",
                        null, null, null, null, null, null, null, AT, AT, List.of()));
            }
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
}
