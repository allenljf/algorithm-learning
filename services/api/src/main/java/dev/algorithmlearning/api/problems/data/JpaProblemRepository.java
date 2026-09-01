package dev.algorithmlearning.api.problems.data;

import dev.algorithmlearning.api.problems.application.*;
import dev.algorithmlearning.api.tags.application.TagRepository;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JpaProblemRepository implements ProblemRepository {
    private final ProblemJpaRepository problems;
    private final TagRepository tags;
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    public JpaProblemRepository(ProblemJpaRepository problems, TagRepository tags, JdbcTemplate jdbc, NamedParameterJdbcTemplate namedJdbc) { this.problems = problems; this.tags = tags; this.jdbc = jdbc; this.namedJdbc = namedJdbc; }
    public Problem save(Problem problem) {
        var saved = problems.save(entity(problem));
        jdbc.update("delete from problem_tags where problem_id = ?", problem.id());
        for (var tag : problem.tags()) jdbc.update("insert into problem_tags (problem_id, tag_id) values (?, ?)", problem.id(), tag.id());
        return map(saved);
    }
    public Optional<Problem> findByIdAndUserId(UUID id, UUID userId) { return problems.findByIdAndUserId(id, userId).map(this::map); }
    public List<Problem> findAllByUserId(UUID userId) { return problems.findByUserIdOrderByUpdatedAtDescIdDesc(userId).stream().map(this::map).toList(); }
    public ProblemPage search(UUID userId, ProblemSearch search) {
        if (!("updatedDesc".equals(search.sort()) || "createdDesc".equals(search.sort()) || "titleAsc".equals(search.sort()))) throw new IllegalArgumentException("Invalid sort.");
        if (search.reviewStatus() != null && !("neverReviewed".equals(search.reviewStatus()) || "due".equals(search.reviewStatus()) || "scheduled".equals(search.reviewStatus()))) throw new IllegalArgumentException("Invalid review status.");
        var tagIds = search.tagIds() == null ? List.<UUID>of() : search.tagIds();
        if (tagIds.size() > 50 || new HashSet<>(tagIds).size() != tagIds.size()) throw new IllegalArgumentException("Tag IDs must be unique and contain at most 50 values.");
        var params = new MapSqlParameterSource().addValue("userId", userId).addValue("q", search.query() == null ? "" : search.query().trim()).addValue("difficulty", search.difficulty()).addValue("platform", search.platform()).addValue("tagIds", tagIds.toArray(UUID[]::new)).addValue("tagCount", tagIds.size()).addValue("reviewStatus", search.reviewStatus()).addValue("limit", search.pageSize()).addValue("offset", (search.page() - 1) * search.pageSize());
        var where = " from problems p where p.user_id=:userId and (:q='' or p.external_problem_id ilike '%' || :q || '%' or problem_search_vector(p.title,p.description,p.notes,p.key_insight,p.mistakes,p.interview_notes) @@ plainto_tsquery('simple', :q)) and (:difficulty is null or p.difficulty=:difficulty) and (:platform is null or p.platform=:platform) and (:tagCount=0 or (select count(distinct pt.tag_id) from problem_tags pt where pt.problem_id=p.id and pt.tag_id=any(cast(:tagIds as uuid[])))=:tagCount) and (:reviewStatus is null or (:reviewStatus='neverReviewed' and not exists (select 1 from reviews r where r.problem_id=p.id)) or (:reviewStatus='due' and exists (select 1 from reviews r where r.problem_id=p.id and r.next_review_at=(select max(r2.next_review_at) from reviews r2 where r2.problem_id=p.id) and r.next_review_at<=now())) or (:reviewStatus='scheduled' and exists (select 1 from reviews r where r.problem_id=p.id and r.next_review_at=(select max(r2.next_review_at) from reviews r2 where r2.problem_id=p.id) and r.next_review_at>now())))";
        var order = switch (search.sort()) { case "createdDesc" -> "p.created_at desc,p.id desc"; case "titleAsc" -> "lower(p.title) asc,p.id asc"; default -> "p.updated_at desc,p.id desc"; };
        var rows = namedJdbc.query("select p.*" + where + " order by " + order + " limit :limit offset :offset", params, (rs, row) -> map(new ProblemEntity((UUID) rs.getObject("id"), (UUID) rs.getObject("user_id"), rs.getString("title"), rs.getString("platform"), rs.getString("external_problem_id"), rs.getString("external_url"), rs.getString("difficulty"), rs.getString("description"), rs.getString("notes"), rs.getString("key_insight"), rs.getString("time_complexity"), rs.getString("space_complexity"), rs.getString("mistakes"), rs.getString("interview_notes"), rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant())));
        var total = namedJdbc.queryForObject("select count(*)" + where, params, Long.class);
        return new ProblemPage(rows, total == null ? 0 : total);
    }
    public void deleteByIdAndUserId(UUID id, UUID userId) { problems.deleteByIdAndUserId(id, userId); }
    private Problem map(ProblemEntity entity) { return new Problem(entity.id, entity.userId, entity.title, entity.platform, entity.externalProblemId, entity.externalUrl, entity.difficulty, entity.description, entity.notes, entity.keyInsight, entity.timeComplexity, entity.spaceComplexity, entity.mistakes, entity.interviewNotes, entity.createdAt, entity.updatedAt, tags.findAllByProblemId(entity.id)); }
    private ProblemEntity entity(Problem problem) { return new ProblemEntity(problem.id(), problem.userId(), problem.title(), problem.platform(), problem.externalProblemId(), problem.externalUrl(), problem.difficulty(), problem.description(), problem.notes(), problem.keyInsight(), problem.timeComplexity(), problem.spaceComplexity(), problem.mistakes(), problem.interviewNotes(), problem.createdAt(), problem.updatedAt()); }
}
