package dev.algorithmlearning.api.problems.application;

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
    void deleteByIdAndUserId(UUID id, UUID userId);
}
