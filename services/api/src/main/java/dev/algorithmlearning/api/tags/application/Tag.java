package dev.algorithmlearning.api.tags.application;

import java.time.Instant;
import java.util.UUID;

public record Tag(UUID id, UUID userId, String name, String normalizedName, Instant createdAt, Instant updatedAt) { }
