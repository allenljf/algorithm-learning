package dev.algorithmlearning.api.auth.application;

import dev.algorithmlearning.api.auth.domain.RefreshTokenService;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public final class AuthSessionService {
    private final AuthSessionRepository sessions;
    private final RefreshTokenService tokens;
    private final Clock clock;

    public AuthSessionService(AuthSessionRepository sessions, RefreshTokenService tokens, Clock clock) {
        this.sessions = sessions;
        this.tokens = tokens;
        this.clock = clock;
    }

    public SessionToken create(UUID userId) {
        var raw = tokens.newToken();
        var now = clock.instant();
        sessions.save(new AuthSession(UUID.randomUUID(), userId, tokens.hash(raw), now.plus(30, ChronoUnit.DAYS), null, null));
        return new SessionToken(userId, raw, now.plus(30, ChronoUnit.DAYS));
    }

    public Optional<SessionToken> rotate(String rawToken) {
        if (rawToken == null) return Optional.empty();
        var now = clock.instant();
        return sessions.findByTokenHash(tokens.hash(rawToken))
                .filter(session -> session.isActiveAt(now))
                .map(session -> {
                    sessions.revoke(session.id(), now);
                    return create(session.userId());
                });
    }

    public boolean revoke(String rawToken) {
        if (rawToken == null) return false;
        var now = clock.instant();
        return sessions.findByTokenHash(tokens.hash(rawToken)).filter(session -> session.isActiveAt(now))
                .map(session -> { sessions.revoke(session.id(), now); return true; }).orElse(false);
    }

    public record SessionToken(UUID userId, String rawToken, Instant expiresAt) { }
}
