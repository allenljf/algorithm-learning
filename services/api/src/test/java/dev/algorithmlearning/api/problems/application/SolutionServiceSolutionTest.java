package dev.algorithmlearning.api.problems.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SolutionServiceSolutionTest {
    @Test
    void listsOnlyOwnedProblemsSolutionsInStableCreationOrderAndHidesForeignSolutions() {
        var owner = UUID.randomUUID(); var other = UUID.randomUUID(); var problemId = UUID.randomUUID();
        var problems = new InMemoryProblems(problem(owner, problemId)); var solutions = new InMemorySolutions(owner);
        var service = new SolutionService(problems, solutions);
        var later = service.create(owner, problemId, new SolutionWrite("java", "class A {}", null));
        var earlier = solutions.save(new Solution(UUID.randomUUID(), problemId, "python", "print(1)", null, Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z")));

        assertThat(service.list(owner, problemId)).extracting(Solution::id).containsExactly(earlier.id(), later.id());
        assertThat(service.findById(other, later.id())).isEmpty();
    }
    private static Problem problem(UUID owner, UUID id) { var now=Instant.now(); return new Problem(id,owner,"P","other",null,null,"easy",null,null,null,null,null,null,null,now,now,List.of()); }
    private static final class InMemoryProblems implements ProblemRepository { private final Problem p; InMemoryProblems(Problem p){this.p=p;} public Problem save(Problem p){return p;} public Optional<Problem> findByIdAndUserId(UUID id,UUID owner){return p.id().equals(id)&&p.userId().equals(owner)?Optional.of(p):Optional.empty();} public List<Problem> findAllByUserId(UUID owner){return List.of();} public void deleteByIdAndUserId(UUID id,UUID owner){} }
    private static final class InMemorySolutions implements SolutionRepository { private final UUID owner; private final List<Solution> values=new ArrayList<>(); InMemorySolutions(UUID owner){this.owner=owner;} public Solution save(Solution value){values.removeIf(v->v.id().equals(value.id()));values.add(value);return value;} public List<Solution> findAllByProblemId(UUID id){return values.stream().filter(v->v.problemId().equals(id)).sorted(java.util.Comparator.comparing(Solution::createdAt).thenComparing(Solution::id)).toList();} public Optional<Solution> findByIdAndUserId(UUID id,UUID userId){return owner.equals(userId)?values.stream().filter(v->v.id().equals(id)).findFirst():Optional.empty();} public void deleteById(UUID id){values.removeIf(v->v.id().equals(id));} }
}
