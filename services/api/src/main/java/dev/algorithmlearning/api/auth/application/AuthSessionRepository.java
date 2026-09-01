package dev.algorithmlearning.api.auth.application;

import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository {
    AuthSession save(AuthSession session);
    Optional<AuthSession> findByTokenHash(byte[] tokenHash);
    void revoke(UUID sessionId, java.time.Instant revokedAt);
}
