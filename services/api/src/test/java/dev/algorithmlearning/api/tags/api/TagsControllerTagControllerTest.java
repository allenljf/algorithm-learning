package dev.algorithmlearning.api.tags.api;

import static org.assertj.core.api.Assertions.assertThat;

import dev.algorithmlearning.api.app.security.CurrentUser;
import dev.algorithmlearning.api.tags.application.Tag;
import dev.algorithmlearning.api.tags.application.TagRepository;
import dev.algorithmlearning.api.tags.application.TagService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class TagsControllerTagControllerTest {

    @AfterEach
    void clearSecurityContext() { SecurityContextHolder.clearContext(); }

    @Test
    void returnsCreatedThenExistingTagForTheAuthenticatedOwner() {
        var owner = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(owner.toString(), null));
        var controller = new TagsController(new TagService(new InMemoryTagRepository(), Clock.fixed(Instant.parse("2026-09-02T00:00:00Z"), ZoneOffset.UTC)), new CurrentUser());

        var created = controller.create(new TagsController.TagCreateRequest("Trees"));
        var existing = controller.create(new TagsController.TagCreateRequest(" trees "));

        assertThat(created.getStatusCode().value()).isEqualTo(201);
        assertThat(created.getHeaders().getLocation()).hasPath("/api/v1/tags/" + created.getBody().id());
        assertThat(existing.getStatusCode().value()).isEqualTo(200);
        assertThat(existing.getBody().id()).isEqualTo(created.getBody().id());
    }

    private static final class InMemoryTagRepository implements TagRepository {
        private final List<Tag> tags = new ArrayList<>();
        public Optional<Tag> findByUserIdAndNormalizedName(UUID userId, String normalizedName) { return tags.stream().filter(tag -> tag.userId().equals(userId) && tag.normalizedName().equals(normalizedName)).findFirst(); }
        public List<Tag> findAllByUserIdAndNormalizedNameContaining(UUID userId, String normalizedQuery) { return tags.stream().filter(tag -> tag.userId().equals(userId) && tag.normalizedName().contains(normalizedQuery)).toList(); }
        public Tag save(Tag tag) { tags.add(tag); return tag; }
    }
}
