package dev.algorithmlearning.api.tags.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface TagJpaRepository extends JpaRepository<TagEntity, UUID> {
    Optional<TagEntity> findByUserIdAndNormalizedName(UUID userId, String normalizedName);
    List<TagEntity> findByUserIdAndNormalizedNameContainingOrderByNameAsc(UUID userId, String normalizedName);
}
