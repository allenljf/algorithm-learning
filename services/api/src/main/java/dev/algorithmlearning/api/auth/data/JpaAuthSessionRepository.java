package dev.algorithmlearning.api.auth.data;

import dev.algorithmlearning.api.auth.application.AuthSession;
import dev.algorithmlearning.api.auth.application.AuthSessionRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAuthSessionRepository implements AuthSessionRepository {
    private final AuthSessionJpaRepository sessions;
    public JpaAuthSessionRepository(AuthSessionJpaRepository sessions) { this.sessions = sessions; }
    public AuthSession save(AuthSession session) { return map(sessions.save(new AuthSessionEntity(session.id(), session.userId(), session.tokenHash(), session.expiresAt(), session.revokedAt(), session.lastUsedAt()))); }
    public Optional<AuthSession> findByTokenHash(byte[] tokenHash) { return sessions.findByTokenHash(tokenHash).map(this::map); }
    public void revoke(UUID sessionId, Instant revokedAt) { sessions.findById(sessionId).ifPresent(session -> { session.revokedAt = revokedAt; session.lastUsedAt = revokedAt; }); }
    private AuthSession map(AuthSessionEntity entity) { return new AuthSession(entity.id, entity.userId, entity.tokenHash, entity.expiresAt, entity.revokedAt, entity.lastUsedAt); }
}
