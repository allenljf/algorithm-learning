package dev.algorithmlearning.api.problems.application;
import java.util.*;
public interface SolutionRepository { Solution save(Solution solution); List<Solution> findAllByProblemId(UUID problemId); Optional<Solution> findByIdAndUserId(UUID id, UUID userId); void deleteById(UUID id); }
