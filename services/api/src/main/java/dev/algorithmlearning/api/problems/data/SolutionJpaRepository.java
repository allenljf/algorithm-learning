package dev.algorithmlearning.api.problems.data;
import java.util.*; import org.springframework.data.jpa.repository.*;
interface SolutionJpaRepository extends JpaRepository<SolutionEntity,UUID>{ List<SolutionEntity> findByProblemIdOrderByCreatedAtAscIdAsc(UUID problemId); @Query(value="select s.* from solutions s join problems p on p.id=s.problem_id where s.id=:id and p.user_id=:userId",nativeQuery=true) Optional<SolutionEntity> findByIdAndUserId(UUID id,UUID userId); }
