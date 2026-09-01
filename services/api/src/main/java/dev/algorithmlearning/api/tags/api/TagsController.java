package dev.algorithmlearning.api.tags.api;

import dev.algorithmlearning.api.app.security.CurrentUser;
import dev.algorithmlearning.api.tags.application.Tag;
import dev.algorithmlearning.api.tags.application.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tags")
public class TagsController {
    private final TagService tags;
    private final CurrentUser currentUser;
    public TagsController(TagService tags, CurrentUser currentUser) { this.tags = tags; this.currentUser = currentUser; }

    @GetMapping
    public List<TagSummary> list(@RequestParam(required = false) String q) {
        return tags.list(currentUser.id(), q).stream().map(TagSummary::from).toList();
    }

    @PostMapping
    public ResponseEntity<TagSummary> create(@Valid @RequestBody TagCreateRequest request) {
        var result = tags.create(currentUser.id(), request.name());
        var response = TagSummary.from(result.tag());
        return result.created()
                ? ResponseEntity.created(URI.create("/api/v1/tags/" + response.id())).body(response)
                : ResponseEntity.ok(response);
    }

    public record TagCreateRequest(@NotBlank @Size(max = 80) String name) { }
    public record TagSummary(java.util.UUID id, String name) { static TagSummary from(Tag tag) { return new TagSummary(tag.id(), tag.name()); } }
}
