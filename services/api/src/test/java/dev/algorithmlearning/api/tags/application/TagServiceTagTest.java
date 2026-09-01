package dev.algorithmlearning.api.tags.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TagServiceTagTest {

    @Test
    void returnsTheExistingTagForTheSameOwnerAndNormalizedName() {
        var repository = new InMemoryTagRepository();
        var service = new TagService(repository, Clock.fixed(Instant.parse("2026-09-02T00:00:00Z"), ZoneOffset.UTC));
        var owner = UUID.randomUUID();

        var first = service.create(owner, "Dynamic   Programming");
        var duplicate = service.create(owner, "  dynamic programming ");

        assertThat(first.created()).isTrue();
        assertThat(duplicate.created()).isFalse();
        assertThat(duplicate.tag().id()).isEqualTo(first.tag().id());
        assertThat(repository.tags).hasSize(1);
    }

    @Test
    void listsOnlyTheAuthenticatedOwnersMatchingTags() {
        var repository = new InMemoryTagRepository();
        var service = new TagService(repository, Clock.systemUTC());
        var owner = UUID.randomUUID();
        service.create(owner, "Graph Theory");
        service.create(owner, "Dynamic Programming");
        service.create(UUID.randomUUID(), "Graphs for another owner");

        assertThat(service.list(owner, "graph")).extracting(Tag::name).containsExactly("Graph Theory");
    }

    private static final class InMemoryTagRepository implements TagRepository {
        private final List<Tag> tags = new ArrayList<>();
        public Optional<Tag> findByUserIdAndNormalizedName(UUID userId, String normalizedName) {
            return tags.stream().filter(tag -> tag.userId().equals(userId) && tag.normalizedName().equals(normalizedName)).findFirst();
        }
        public List<Tag> findAllByUserIdAndNormalizedNameContaining(UUID userId, String normalizedQuery) {
            return tags.stream().filter(tag -> tag.userId().equals(userId) && tag.normalizedName().contains(normalizedQuery)).toList();
        }
        public Tag save(Tag tag) { tags.add(tag); return tag; }
    }
}
