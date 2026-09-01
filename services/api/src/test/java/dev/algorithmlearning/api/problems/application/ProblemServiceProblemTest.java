package dev.algorithmlearning.api.problems.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import dev.algorithmlearning.api.tags.application.Tag;
import org.junit.jupiter.api.Test;

class ProblemServiceProblemTest {
    @Test
    void replacesAnOwnedProblemAndHidesItFromAnotherOwner() {
        var repository = new InMemoryProblemRepository();
        var service = new ProblemService(repository, new InMemoryTagRepository(), Clock.fixed(Instant.parse("2026-09-02T00:00:00Z"), ZoneOffset.UTC));
        var owner = UUID.randomUUID();
        var created = service.create(owner, new ProblemWrite("Two Sum", "leetcode", "1", "https://leetcode.com/problems/two-sum", "easy", null, null, null, null, null, null, null, List.of()));

        var replaced = service.replace(owner, created.id(), new ProblemWrite("Three Sum", "leetcode", "15", "https://leetcode.com/problems/3sum", "medium", null, null, null, null, null, null, null, List.of()));

        assertThat(replaced).isPresent();
        assertThat(replaced.orElseThrow().title()).isEqualTo("Three Sum");
        assertThat(service.findById(UUID.randomUUID(), created.id())).isEmpty();
    }

    @Test
    void replacesOwnedTagLinksAtomicallyAndRejectsAnotherOwnersTag() {
        var problems = new InMemoryProblemRepository();
        var tags = new InMemoryTagRepository();
        var service = new ProblemService(problems, tags, Clock.fixed(Instant.parse("2026-09-02T00:00:00Z"), ZoneOffset.UTC));
        var owner = UUID.randomUUID();
        var firstTag = tags.save(tag(owner, "array"));
        var secondTag = tags.save(tag(owner, "hash map"));
        var foreignTag = tags.save(tag(UUID.randomUUID(), "foreign"));

        var created = service.create(owner, write(List.of(firstTag.id())));
        assertThat(created.tags()).extracting(Tag::id).containsExactly(firstTag.id());

        var replaced = service.replace(owner, created.id(), write(List.of(secondTag.id()))).orElseThrow();
        assertThat(replaced.tags()).extracting(Tag::id).containsExactly(secondTag.id());
        assertThatThrownBy(() -> service.replace(owner, created.id(), write(List.of(foreignTag.id()))))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(service.findById(owner, created.id()).orElseThrow().tags()).extracting(Tag::id).containsExactly(secondTag.id());
    }

    private static ProblemWrite write(List<UUID> tagIds) {
        return new ProblemWrite("Two Sum", "leetcode", "1", "https://leetcode.com/problems/two-sum", "easy", null, null, null, null, null, null, null, tagIds);
    }

    private static Tag tag(UUID userId, String name) {
        var now = Instant.parse("2026-09-02T00:00:00Z");
        return new Tag(UUID.randomUUID(), userId, name, name, now, now);
    }

    private static final class InMemoryProblemRepository implements ProblemRepository {
        private final List<Problem> problems = new ArrayList<>();
        public Problem save(Problem problem) { problems.removeIf(value -> value.id().equals(problem.id())); problems.add(problem); return problem; }
        public Optional<Problem> findByIdAndUserId(UUID id, UUID userId) { return problems.stream().filter(problem -> problem.id().equals(id) && problem.userId().equals(userId)).findFirst(); }
        public List<Problem> findAllByUserId(UUID userId) { return problems.stream().filter(problem -> problem.userId().equals(userId)).toList(); }
        public void deleteByIdAndUserId(UUID id, UUID userId) { problems.removeIf(problem -> problem.id().equals(id) && problem.userId().equals(userId)); }
    }

    private static final class InMemoryTagRepository implements dev.algorithmlearning.api.tags.application.TagRepository {
        private final List<Tag> tags = new ArrayList<>();
        public Optional<Tag> findByUserIdAndNormalizedName(UUID userId, String normalizedName) { return tags.stream().filter(tag -> tag.userId().equals(userId) && tag.normalizedName().equals(normalizedName)).findFirst(); }
        public List<Tag> findAllByUserIdAndNormalizedNameContaining(UUID userId, String normalizedQuery) { return tags.stream().filter(tag -> tag.userId().equals(userId) && tag.normalizedName().contains(normalizedQuery)).toList(); }
        public List<Tag> findAllByUserIdAndIdIn(UUID userId, List<UUID> tagIds) { return tags.stream().filter(tag -> tag.userId().equals(userId) && tagIds.contains(tag.id())).toList(); }
        public List<Tag> findAllByProblemId(UUID problemId) { return List.of(); }
        public Tag save(Tag tag) { tags.add(tag); return tag; }
    }
}
