package dev.algorithmlearning.api.tags.data;

import dev.algorithmlearning.api.tags.application.Tag;
import dev.algorithmlearning.api.tags.application.TagRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaTagRepository implements TagRepository {
    private final TagJpaRepository tags;
    public JpaTagRepository(TagJpaRepository tags) { this.tags = tags; }
    public Optional<Tag> findByUserIdAndNormalizedName(UUID userId, String normalizedName) { return tags.findByUserIdAndNormalizedName(userId, normalizedName).map(this::map); }
    public List<Tag> findAllByUserIdAndNormalizedNameContaining(UUID userId, String normalizedQuery) { return tags.findByUserIdAndNormalizedNameContainingOrderByNameAsc(userId, normalizedQuery).stream().map(this::map).toList(); }
    public List<Tag> findAllByUserIdAndIdIn(UUID userId, List<UUID> tagIds) { return tagIds.isEmpty() ? List.of() : tags.findByUserIdAndIdIn(userId, tagIds).stream().map(this::map).toList(); }
    public List<Tag> findAllByProblemId(UUID problemId) { return tags.findAllByProblemId(problemId).stream().map(this::map).toList(); }
    public Tag save(Tag tag) { return map(tags.save(new TagEntity(tag.id(), tag.userId(), tag.name(), tag.normalizedName(), tag.createdAt(), tag.updatedAt()))); }
    private Tag map(TagEntity entity) { return new Tag(entity.id, entity.userId, entity.name, entity.normalizedName, entity.createdAt, entity.updatedAt); }
}
