package dev.algorithmlearning.api.problems.application;

import dev.algorithmlearning.api.problems.domain.ExternalUrlPolicy;
import dev.algorithmlearning.api.tags.application.TagRepository;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public class ProblemService {
    private final ProblemRepository problems;
    private final TagRepository tags;
    private final Clock clock;
    public ProblemService(ProblemRepository problems, TagRepository tags, Clock clock) { this.problems = problems; this.tags = tags; this.clock = clock; }

    @Transactional public Problem create(UUID userId, ProblemWrite write) {
        var now = clock.instant();
        return problems.save(from(UUID.randomUUID(), userId, write, now, now, ownedTags(userId, write.tagIds())));
    }
    @Transactional public Optional<Problem> replace(UUID userId, UUID id, ProblemWrite write) {
        return problems.findByIdAndUserId(id, userId).map(existing -> problems.save(from(id, userId, write, existing.createdAt(), clock.instant(), ownedTags(userId, write.tagIds()))));
    }
    public Optional<Problem> findById(UUID userId, UUID id) { return problems.findByIdAndUserId(id, userId); }
    public List<Problem> list(UUID userId) { return problems.findAllByUserId(userId); }
    public ProblemPage search(UUID userId, ProblemSearch search) { return problems.search(userId, search); }
    @Transactional public boolean delete(UUID userId, UUID id) { if (problems.findByIdAndUserId(id, userId).isEmpty()) return false; problems.deleteByIdAndUserId(id, userId); return true; }

    private java.util.List<dev.algorithmlearning.api.tags.application.Tag> ownedTags(UUID userId, java.util.List<UUID> tagIds) {
        var requested = tagIds == null ? java.util.List.<UUID>of() : tagIds;
        if (requested.size() > 50 || new java.util.HashSet<>(requested).size() != requested.size()) throw new IllegalArgumentException("Tag IDs must be unique and contain at most 50 values.");
        var owned = tags.findAllByUserIdAndIdIn(userId, requested);
        if (owned.size() != requested.size()) throw new IllegalArgumentException("Every tag must be owned by the caller.");
        return owned.stream().sorted(java.util.Comparator.comparing(dev.algorithmlearning.api.tags.application.Tag::name)).toList();
    }

    private static Problem from(UUID id, UUID userId, ProblemWrite write, java.time.Instant createdAt, java.time.Instant updatedAt, java.util.List<dev.algorithmlearning.api.tags.application.Tag> tags) {
        var title = required(write.title(), 200, "Title");
        var platform = required(write.platform(), 20, "Platform");
        if (!(platform.equals("leetcode") || platform.equals("hacker_rank") || platform.equals("other"))) throw new IllegalArgumentException("Invalid platform.");
        var difficulty = required(write.difficulty(), 10, "Difficulty");
        if (!(difficulty.equals("easy") || difficulty.equals("medium") || difficulty.equals("hard"))) throw new IllegalArgumentException("Invalid difficulty.");
        var externalUrl = optional(write.externalUrl(), 2000); ExternalUrlPolicy.requireAllowed(platform, externalUrl);
        return new Problem(id, userId, title, platform, optional(write.externalProblemId(), 120), externalUrl, difficulty,
                optional(write.description(), 20000), optional(write.notes(), 20000), optional(write.keyInsight(), 10000), optional(write.timeComplexity(), 200), optional(write.spaceComplexity(), 200), optional(write.mistakes(), 20000), optional(write.interviewNotes(), 20000), createdAt, updatedAt, tags);
    }
    private static String required(String value, int max, String name) { var normalized = optional(value, max); if (normalized == null) throw new IllegalArgumentException(name + " is required."); return normalized; }
    private static String optional(String value, int max) { if (value == null) return null; var normalized = value.trim(); if (normalized.isEmpty()) return null; if (normalized.codePointCount(0, normalized.length()) > max) throw new IllegalArgumentException("Value is too long."); return normalized; }
}
