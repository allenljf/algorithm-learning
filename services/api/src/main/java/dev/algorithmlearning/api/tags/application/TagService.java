package dev.algorithmlearning.api.tags.application;

import dev.algorithmlearning.api.tags.domain.TagNameNormalizer;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public final class TagService {
    private final TagRepository tags;
    private final Clock clock;

    public TagService(TagRepository tags, Clock clock) { this.tags = tags; this.clock = clock; }

    @Transactional
    public CreateResult create(UUID userId, String rawName) {
        var normalized = TagNameNormalizer.normalize(rawName);
        var existing = tags.findByUserIdAndNormalizedName(userId, normalized.comparisonKey());
        if (existing.isPresent()) return new CreateResult(existing.get(), false);
        var now = clock.instant();
        var tag = tags.save(new Tag(UUID.randomUUID(), userId, normalized.displayName(), normalized.comparisonKey(), now, now));
        return new CreateResult(tag, true);
    }

    public List<Tag> list(UUID userId, String query) {
        var normalizedQuery = query == null || query.isBlank() ? "" : TagNameNormalizer.normalize(query).comparisonKey();
        return tags.findAllByUserIdAndNormalizedNameContaining(userId, normalizedQuery);
    }

    public record CreateResult(Tag tag, boolean created) { }
}
