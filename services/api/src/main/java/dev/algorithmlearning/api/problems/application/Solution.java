package dev.algorithmlearning.api.problems.application;
import java.time.Instant; import java.util.UUID;
public record Solution(UUID id, UUID problemId, String language, String code, String explanation, Instant createdAt, Instant updatedAt) { }
