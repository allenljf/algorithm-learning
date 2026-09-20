package dev.algorithmlearning.api.reviews.data;

import dev.algorithmlearning.api.reviews.application.ProblemReviewState;
import dev.algorithmlearning.api.reviews.application.Review;
import dev.algorithmlearning.api.reviews.application.ReviewRepository;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JpaReviewRepository implements ReviewRepository {

    private static final String COLUMNS =
            "r.id, r.problem_id, r.confidence, r.reviewed_at, r.next_review_at, r.notes, r.policy_version, "
                    + "r.previous_interval_days, r.previous_ease_factor, r.interval_days, r.ease_factor, r.repetitions";

    private static final RowMapper<Review> REVIEW_MAPPER = (rs, row) -> new Review(
            (UUID) rs.getObject("id"),
            (UUID) rs.getObject("problem_id"),
            rs.getInt("confidence"),
            rs.getTimestamp("reviewed_at").toInstant(),
            rs.getTimestamp("next_review_at").toInstant(),
            rs.getString("notes"),
            rs.getString("policy_version"),
            (Integer) rs.getObject("previous_interval_days"),
            rs.getBigDecimal("previous_ease_factor"),
            (Integer) rs.getObject("interval_days"),
            rs.getBigDecimal("ease_factor"),
            (Integer) rs.getObject("repetitions"));

    private final NamedParameterJdbcTemplate jdbc;

    public JpaReviewRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Review save(Review review) {
        // Instants must be bound as OffsetDateTime: pgjdbc cannot infer a type
        // for java.time.Instant, so a bean parameter source fails at runtime.
        var params = new MapSqlParameterSource()
                .addValue("id", review.id())
                .addValue("problemId", review.problemId())
                .addValue("confidence", review.confidence())
                .addValue("reviewedAt", OffsetDateTime.ofInstant(review.reviewedAt(), ZoneOffset.UTC))
                .addValue("nextReviewAt", OffsetDateTime.ofInstant(review.nextReviewAt(), ZoneOffset.UTC))
                .addValue("notes", review.notes())
                .addValue("policyVersion", review.policyVersion())
                .addValue("previousIntervalDays", review.previousIntervalDays())
                .addValue("previousEaseFactor", review.previousEaseFactor())
                .addValue("intervalDays", review.intervalDays())
                .addValue("easeFactor", review.easeFactor())
                .addValue("repetitions", review.repetitions());
        jdbc.update("insert into reviews (id, problem_id, confidence, reviewed_at, next_review_at, notes, policy_version, "
                        + "previous_interval_days, previous_ease_factor, interval_days, ease_factor, repetitions) values "
                        + "(:id, :problemId, :confidence, :reviewedAt, :nextReviewAt, :notes, :policyVersion, "
                        + ":previousIntervalDays, :previousEaseFactor, :intervalDays, :easeFactor, :repetitions)",
                params);
        return review;
    }

    @Override
    public Optional<Review> latest(UUID userId, UUID problemId) {
        var params = new MapSqlParameterSource().addValue("u", userId).addValue("p", problemId);
        return jdbc.query("select " + COLUMNS + " from reviews r join problems p on p.id = r.problem_id "
                        + "where p.user_id = :u and r.problem_id = :p order by r.reviewed_at desc, r.id desc limit 1",
                params, REVIEW_MAPPER).stream().findFirst();
    }

    @Override
    public List<UUID> dueProblemIds(UUID userId, Instant now, int limit, int offset) {
        var params = new MapSqlParameterSource()
                .addValue("u", userId)
                .addValue("now", OffsetDateTime.ofInstant(now, ZoneOffset.UTC))
                .addValue("l", limit)
                .addValue("o", offset);
        return jdbc.query("select p.id from problems p "
                        + "left join lateral (select r.next_review_at from reviews r where r.problem_id = p.id "
                        + "order by r.reviewed_at desc, r.id desc limit 1) latest on true "
                        + "where p.user_id = :u and coalesce(latest.next_review_at, p.created_at) <= :now "
                        + "order by coalesce(latest.next_review_at, p.created_at) asc, p.id asc limit :l offset :o",
                params, (rs, row) -> (UUID) rs.getObject("id"));
    }

    @Override
    public Map<UUID, ProblemReviewState> reviewStates(UUID userId, Collection<UUID> problemIds) {
        if (problemIds == null || problemIds.isEmpty()) return Map.of();
        var params = new MapSqlParameterSource()
                .addValue("u", userId).addValue("ids", problemIds.toArray(UUID[]::new));
        var states = new LinkedHashMap<UUID, ProblemReviewState>();
        jdbc.query("select distinct on (r.problem_id) " + COLUMNS
                        + ", count(*) over (partition by r.problem_id) as review_count "
                        + "from reviews r join problems p on p.id = r.problem_id "
                        + "where p.user_id = :u and r.problem_id = any(cast(:ids as uuid[])) "
                        + "order by r.problem_id, r.reviewed_at desc, r.id desc",
                params, (rs, row) -> {
                    var review = REVIEW_MAPPER.mapRow(rs, row);
                    return new ProblemReviewState(review, rs.getInt("review_count"));
                })
                .forEach(state -> states.put(state.latest().problemId(), state));
        return states;
    }

    @Override
    public List<Review> history(UUID userId, UUID problemId, int limit, int offset) {
        var params = new MapSqlParameterSource()
                .addValue("u", userId).addValue("p", problemId).addValue("l", limit).addValue("o", offset);
        return jdbc.query("select " + COLUMNS + " from reviews r join problems p on p.id = r.problem_id "
                        + "where p.user_id = :u and r.problem_id = :p order by r.reviewed_at desc, r.id desc limit :l offset :o",
                params, REVIEW_MAPPER);
    }
}
