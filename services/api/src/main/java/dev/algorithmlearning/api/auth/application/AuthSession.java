package dev.algorithmlearning.api.auth.application;

import java.time.Instant;
import java.util.UUID;

public record AuthSession(UUID id, UUID userId, byte[] tokenHash, Instant expiresAt, Instant revokedAt, Instant lastUsedAt) {
    public boolean isActiveAt(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }
}
