package dev.algorithmlearning.api.auth.application;

import java.time.Instant;
import java.util.UUID;

public record AuthUser(UUID id, String email, String passwordHash, Instant createdAt) { }
