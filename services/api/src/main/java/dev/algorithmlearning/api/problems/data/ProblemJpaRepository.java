package dev.algorithmlearning.api.problems.data;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
interface ProblemJpaRepository extends JpaRepository<ProblemEntity,UUID>{ Optional<ProblemEntity> findByIdAndUserId(UUID id,UUID userId); List<ProblemEntity> findByUserIdOrderByUpdatedAtDescIdDesc(UUID userId); void deleteByIdAndUserId(UUID id,UUID userId); }
