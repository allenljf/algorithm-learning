package dev.algorithmlearning.api.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import dev.algorithmlearning.api.auth.domain.RefreshTokenService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthSessionRotationAuthTest {

    @Test
    void rotatesAnActiveSessionAndRejectsThePreviouslyPresentedToken() {
        var now = Instant.parse("2026-09-01T00:00:00Z");
        var tokens = new RefreshTokenService("test-server-side-key-material");
        var sessions = new InMemoryAuthSessionRepository();
        var service = new AuthSessionService(sessions, tokens, Clock.fixed(now, ZoneOffset.UTC));
        var userId = UUID.randomUUID();
        var original = service.create(userId);

        var rotated = service.rotate(original.rawToken()).orElseThrow();

        assertThat(rotated.userId()).isEqualTo(userId);
        assertThat(rotated.rawToken()).isNotEqualTo(original.rawToken());
        assertThat(service.rotate(original.rawToken())).isEmpty();
    }

    private static final class InMemoryAuthSessionRepository implements AuthSessionRepository {
        private final Map<UUID, AuthSession> sessions = new HashMap<>();
        public AuthSession save(AuthSession session) { sessions.put(session.id(), session); return session; }
        public Optional<AuthSession> findByTokenHash(byte[] hash) {
            return sessions.values().stream().filter(session -> java.security.MessageDigest.isEqual(session.tokenHash(), hash)).findFirst();
        }
        public void revoke(UUID id, Instant revokedAt) {
            var session = sessions.get(id);
            sessions.put(id, new AuthSession(session.id(), session.userId(), session.tokenHash(), session.expiresAt(), revokedAt, revokedAt));
        }
    }
}
