package dev.algorithmlearning.api.tags.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface TagJpaRepository extends JpaRepository<TagEntity, UUID> {
    Optional<TagEntity> findByUserIdAndNormalizedName(UUID userId, String normalizedName);
    List<TagEntity> findByUserIdAndNormalizedNameContainingOrderByNameAsc(UUID userId, String normalizedName);
    List<TagEntity> findByUserIdAndIdIn(UUID userId, List<UUID> ids);
    @org.springframework.data.jpa.repository.Query(value = "select t.* from tags t join problem_tags pt on pt.tag_id = t.id where pt.problem_id = :problemId order by t.name asc", nativeQuery = true)
    List<TagEntity> findAllByProblemId(UUID problemId);
}
