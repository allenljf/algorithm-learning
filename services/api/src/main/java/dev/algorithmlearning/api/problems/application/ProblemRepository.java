package dev.algorithmlearning.api.problems.application;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemRepository {
    Problem save(Problem problem);
    Optional<Problem> findByIdAndUserId(UUID id, UUID userId);
    List<Problem> findAllByUserId(UUID userId);
    default ProblemPage search(UUID userId, ProblemSearch search) {
        var all = findAllByUserId(userId);
        return new ProblemPage(all, all.size());
    }
    default List<Problem> findAllByIdsAndUserId(UUID userId, Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        var wanted = new HashSet<>(ids);
        return findAllByUserId(userId).stream().filter(problem -> wanted.contains(problem.id())).toList();
    }
    void deleteByIdAndUserId(UUID id, UUID userId);
}
