package dev.algorithmlearning.api.tags.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository {
    Optional<Tag> findByUserIdAndNormalizedName(UUID userId, String normalizedName);
    List<Tag> findAllByUserIdAndNormalizedNameContaining(UUID userId, String normalizedQuery);
    default List<Tag> findAllByUserIdAndIdIn(UUID userId, List<UUID> tagIds) { return List.of(); }
    default List<Tag> findAllByProblemId(UUID problemId) { return List.of(); }
    Tag save(Tag tag);
}
